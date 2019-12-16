package org.zalando.opentracing.jdbc;

import io.opentracing.Scope;
import io.opentracing.mock.MockSpan;
import io.opentracing.mock.MockTracer;
import org.h2.jdbcx.JdbcDataSource;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasEntry;
import static org.hamcrest.Matchers.hasKey;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.zalando.opentracing.jdbc.Lifecycle.composite;

class ActiveSpanLifecycleTest {

    private final MockTracer tracer = new MockTracer();
    private final DataSourceTracer unit = new DataSourceTracer(tracer)
            .withLifecycle(composite(
                    new ActiveSpanLifecycle(),
                    new NewSpanLifecycle()
            ));
    private final JdbcDataSource original = new JdbcDataSource();

    ActiveSpanLifecycleTest() {
        original.setUrl("jdbc:h2:mem:users;DB_CLOSE_DELAY=-1");
    }

    @BeforeEach
    @AfterEach
    void setUp() throws SQLException {
        try (final Connection connection = original.getConnection();
             final Statement statement = connection.createStatement()) {

            statement.execute("DROP ALL OBJECTS");
        }
    }

    @Test
    void shouldUseActiveSpan() throws SQLException {
        final DataSource dataSource = unit.trace(original);

        try (final Connection connection = dataSource.getConnection();
             final Statement statement = connection.createStatement()) {

            final MockSpan parent = tracer.buildSpan("parent").start();
            try (final Scope ignored = tracer.activateSpan(parent)) {
                statement.execute("SELECT 1");
            } finally {
                parent.finish();
            }
        }

        final List<MockSpan> spans = tracer.finishedSpans();
        final MockSpan span = Iterables.getOnlyElement(spans);

        assertThat(span.operationName(), is("parent"));
        assertThat(span.tags(), hasEntry("component", "JDBC"));
        assertThat(span.tags(), hasEntry("db.instance", "USERS"));
        assertThat(span.tags(), hasEntry("db.statement", "SELECT 1"));
        assertThat(span.tags(), hasEntry("db.type", "sql"));
        assertThat(span.tags(), hasEntry("db.user", ""));
        assertThat(span.tags(), hasEntry("peer.hostname", "users"));
        assertThat(span.tags(), not(hasKey("peer.port")));

        assertThat(span.tags(), not(hasKey("span.kind")));
    }

}
