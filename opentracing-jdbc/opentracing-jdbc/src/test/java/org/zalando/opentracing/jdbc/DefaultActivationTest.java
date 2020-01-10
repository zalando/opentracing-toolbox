package org.zalando.opentracing.jdbc;

import com.github.valfirst.slf4jtest.LoggingEvent;
import com.github.valfirst.slf4jtest.TestLogger;
import com.github.valfirst.slf4jtest.TestLoggerFactory;
import com.github.valfirst.slf4jtest.TestLoggerFactoryExtension;
import io.opentracing.Tracer;
import io.opentracing.mock.MockTracer;
import net.ttddyy.dsproxy.listener.logging.SLF4JQueryLoggingListener;
import net.ttddyy.dsproxy.support.ProxyDataSourceBuilder;
import org.h2.jdbcx.JdbcDataSource;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.zalando.opentracing.proxy.plugin.LogCorrelation;
import org.zalando.opentracing.proxy.core.ProxyTracer;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasKey;
import static org.hamcrest.Matchers.hasSize;

@ExtendWith(TestLoggerFactoryExtension.class)
class DefaultActivationTest {

    private final TestLogger logger =
            TestLoggerFactory.getTestLogger(SLF4JQueryLoggingListener.class);

    private final Tracer tracer = new ProxyTracer(new MockTracer())
            .with(new LogCorrelation().withTraceId("trace_id"));

    private final DataSourceTracer unit = new DataSourceTracer(tracer)
            .withActivation(new DefaultActivation());

    private final DataSource original =
            ProxyDataSourceBuilder.create(createDataSource())
                    .logQueryBySlf4j()
            .build();

    private static JdbcDataSource createDataSource() {
        final JdbcDataSource dataSource = new JdbcDataSource();
        dataSource.setUrl("jdbc:h2:mem:users;DB_CLOSE_DELAY=-1");
        return dataSource;
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
    void shouldActivateSpan() throws SQLException {
        final DataSource dataSource = unit.trace(original);

        try (final Connection connection = dataSource.getConnection();
             final Statement statement = connection.createStatement()) {

            statement.execute("SELECT 1");
        }

        final List<LoggingEvent> events = logger.getLoggingEvents();

        assertThat(events, hasSize(1));
        final LoggingEvent event = events.get(0);

        assertThat(event.getMdc(), hasKey("trace_id"));
    }

}
