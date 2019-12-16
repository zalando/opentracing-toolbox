package org.zalando.opentracing.jdbc;

import io.opentracing.mock.MockSpan;
import io.opentracing.mock.MockTracer;
import org.h2.jdbcx.JdbcDataSource;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.zalando.opentracing.jdbc.operation.StoredProcedureOperationName;
import org.zalando.opentracing.jdbc.span.PeerAddressSpanDecorator;
import org.zalando.opentracing.jdbc.span.StaticSpanDecorator;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Collection;
import java.util.List;

import static java.util.Collections.singletonMap;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasEntry;
import static org.hamcrest.Matchers.hasKey;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.zalando.opentracing.jdbc.Iterables.getOnlyElement;

class DataSourceTracerTest {

    private final MockTracer tracer = new MockTracer();
    private final DataSourceTracer unit = new DataSourceTracer(tracer)
            .withAdditionalSpanDecorators(new PeerAddressSpanDecorator())
            .withAdditionalSpanDecorators(new StaticSpanDecorator(singletonMap("flow_id", "REcCvlqMSReeo7adheiYFA")));
    private final JdbcDataSource original = new JdbcDataSource();

    DataSourceTracerTest() {
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
    void shouldTraceStatement() throws SQLException {
        final DataSource dataSource = unit.trace(original);

        try (final Connection connection = dataSource.getConnection();
             final Statement statement = connection.createStatement()) {

            statement.execute("SELECT 1");
        }

        final List<MockSpan> spans = tracer.finishedSpans();
        final MockSpan span = getOnlyElement(spans);

        assertThat(span.operationName(), is("execute"));
        assertThat(span.tags(), hasEntry("component", "JDBC"));
        assertThat(span.tags(), hasEntry("db.instance", "USERS"));
        assertThat(span.tags(), hasEntry("db.statement", "SELECT 1"));
        assertThat(span.tags(), hasEntry("db.type", "sql"));
        assertThat(span.tags(), hasEntry("db.user", ""));
        assertThat(span.tags(), hasEntry("peer.address", "h2:mem:users"));
        assertThat(span.tags(), hasEntry("peer.hostname", "users"));
        assertThat(span.tags(), not(hasKey("peer.port")));
        assertThat(span.tags(), hasEntry("span.kind", "client"));
        assertThat(span.tags(), hasEntry("flow_id", "REcCvlqMSReeo7adheiYFA"));

        assertThat(span.tags(), not(hasKey("error")));
    }

    @Test
    void shouldTracePreparedStatement() throws SQLException {
        final DataSource dataSource = unit.trace(original);

        try (final Connection connection = dataSource.getConnection();
             final PreparedStatement statement = connection.prepareStatement("SELECT ?")) {

            statement.setInt(1, 1);
            statement.execute();
        }

        final List<MockSpan> spans = tracer.finishedSpans();
        final MockSpan span = getOnlyElement(spans);

        assertThat(span.operationName(), is("execute"));
        assertThat(span.tags(), hasEntry("component", "JDBC"));
        assertThat(span.tags(), hasEntry("db.instance", "USERS"));
        assertThat(span.tags(), hasEntry("db.statement", "SELECT ?"));
        assertThat(span.tags(), hasEntry("db.type", "sql"));
        assertThat(span.tags(), hasEntry("span.kind", "client"));
        assertThat(span.tags(), hasEntry("flow_id", "REcCvlqMSReeo7adheiYFA"));

        assertThat(span.tags(), not(hasKey("error")));
    }

    @Test
    void shouldTraceBatch() throws SQLException {
        try (final Connection connection = original.getConnection();
             final Statement statement = connection.createStatement()) {

            statement.execute("CREATE TABLE users(id BIGINT, name TEXT)");
        }

        final DataSource dataSource = unit.trace(original);

        try (final Connection connection = dataSource.getConnection();
             final Statement statement = connection.createStatement()) {

            statement.addBatch("INSERT INTO users(id, name) VALUES (1, 'Alice')");
            statement.addBatch("INSERT INTO users(id, name) VALUES (2, 'Bob')");
            statement.executeBatch();
        }

        final List<MockSpan> spans = tracer.finishedSpans();
        final MockSpan span = getOnlyElement(spans);

        assertThat(span.operationName(), is("executeBatch"));
        assertThat(span.tags(), hasEntry("db.statement",
                "INSERT INTO users(id, name) VALUES (1, 'Alice')\nINSERT INTO users(id, name) VALUES (2, 'Bob')"));
    }

    @Test
    void shouldTracePreparedBatch() throws SQLException {
        try (final Connection connection = original.getConnection();
             final Statement statement = connection.createStatement()) {

            statement.execute("CREATE TABLE users(id BIGINT, name TEXT)");
        }

        final DataSource dataSource = unit.trace(original);

        final String sql = "INSERT INTO users(id, name) VALUES (?, ?)";
        try (final Connection connection = dataSource.getConnection();
             final PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setInt(1, 1);
            statement.setString(2, "Alice");
            statement.addBatch();

            statement.setInt(1, 2);
            statement.setString(2, "Bob");
            statement.addBatch();

            statement.executeBatch();
        }

        final List<MockSpan> spans = tracer.finishedSpans();
        final MockSpan span = getOnlyElement(spans);

        assertThat(span.operationName(), is("executeBatch"));
        assertThat(span.tags(), hasEntry("db.statement", "INSERT INTO users(id, name) VALUES (?, ?)"));
    }

    @Test
    void shouldUseCustomOperationName() throws SQLException {
        try (final Connection connection = original.getConnection()) {
            final String sql = "CREATE ALIAS MATRIX FOR \"org.zalando.opentracing.jdbc.Matrix.create\";";
            try (final PreparedStatement statement = connection.prepareStatement(sql)) {
                statement.execute();
            }
        }

        final DataSource dataSource = unit
                .withOperationName(new StoredProcedureOperationName())
                .trace(original);

        try (final Connection connection = dataSource.getConnection()) {
            try (final Statement statement = connection.createStatement()) {
                statement.execute("SELECT * FROM MATRIX(4)");
                statement.execute("SELECT 1");
            }
        }

        final List<MockSpan> spans = tracer.finishedSpans();
        assertThat(spans, hasSize(2));

        assertThat(spans.get(0).operationName(), is("MATRIX"));
        assertThat(spans.get(1).operationName(), is("execute"));
    }

    @Test
    void shouldTraceErrors() {
        final DataSource dataSource = unit.trace(original);

        assertThrows(SQLException.class, () -> {
            try (final Connection connection = dataSource.getConnection();
                 final Statement statement = connection.createStatement()) {

                statement.execute("SELECT * FROM MATRIX(4)");
            }
        });

        final List<MockSpan> spans = tracer.finishedSpans();
        final MockSpan span = getOnlyElement(spans);

        assertThat(span.operationName(), is("execute"));
        assertThat(span.tags(), hasEntry("component", "JDBC"));
        assertThat(span.tags(), hasEntry("db.statement", "SELECT * FROM MATRIX(4)"));
        assertThat(span.tags(), hasEntry("db.type", "sql"));
        assertThat(span.tags(), hasEntry("span.kind", "client"));
        assertThat(span.tags(), hasEntry("flow_id", "REcCvlqMSReeo7adheiYFA"));
        assertThat(span.tags(), hasEntry("error", true));

        final List<MockSpan.LogEntry> entries = span.logEntries();
        assertThat(entries, hasSize(3));

        assertThat(entries.get(0).fields().get("message").toString(),
                containsString("Function \"MATRIX\" not found"));

        assertThat(entries.get(1).fields(), hasEntry("error.kind", "JdbcSQLSyntaxErrorException"));
        assertThat(entries.get(1).fields(), hasEntry(equalTo("error.object"), instanceOf(SQLException.class)));

        assertThat(entries.get(2).fields().get("stack").toString(), containsString("at org.h2.jdbc"));
    }

}
