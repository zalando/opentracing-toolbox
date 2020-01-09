package org.zalando.opentracing.jdbc.autoconfigure;

import io.opentracing.mock.MockSpan;
import io.opentracing.mock.MockTracer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Map;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasEntry;
import static org.hamcrest.Matchers.is;
import static org.junit.platform.commons.util.CollectionUtils.getOnlyElement;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.NONE;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = NONE)
@EnableAutoConfiguration
class DefaultConfigurationTest {

    @Configuration
    static class TestConfiguration {

        @Bean
        public MockTracer tracer() {
            return new MockTracer();
        }

    }

    @Autowired
    private DataSource dataSource;

    @Autowired
    private MockTracer tracer;

    @BeforeEach
    @AfterEach
    void setUp() {
        tracer.reset();
    }

    @Test
    void tracesDataSource() throws SQLException {
        verify(dataSource.getConnection());
    }

    @Test
    void tracesDataSourceWhenConnectingExplicitly() throws SQLException {
        verify(dataSource.getConnection("sa", "password"));
    }

    private void verify(final Connection connection) throws SQLException {
        try (final Statement statement = connection.createStatement()) {

            statement.execute("SELECT 1");
        } finally {
            connection.close();
        }

        final MockSpan span = getOnlyElement(tracer.finishedSpans());
        final Map<String, Object> tags = span.tags();

        assertThat(span.operationName(), is("execute"));
        assertThat(tags, hasEntry("span.kind", "client"));
        assertThat(tags, hasEntry("component", "JDBC"));
        assertThat(tags, hasEntry("db.statement", "SELECT 1"));
        assertThat(tags, hasEntry("db.type", "sql"));
    }

}
