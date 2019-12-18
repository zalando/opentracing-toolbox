package org.zalando.opentracing.jdbc.autoconfigure;

import io.opentracing.Tracer;
import io.opentracing.mock.MockSpan;
import io.opentracing.mock.MockTracer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.zalando.opentracing.jdbc.DataSourceTracer;
import org.zalando.opentracing.jdbc.span.ComponentSpanDecorator;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Map;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.aMapWithSize;
import static org.hamcrest.Matchers.hasEntry;
import static org.junit.platform.commons.util.CollectionUtils.getOnlyElement;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.NONE;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = NONE)
@EnableAutoConfiguration
class CustomDataSourceTracerTest {

    @Configuration
    static class TestConfiguration {

        @Bean
        public MockTracer tracer() {
            return new MockTracer();
        }

        @Bean
        public DataSourceTracer dataSourceTracer(final Tracer tracer) {
            return new DataSourceTracer(tracer)
                    .withSpanDecorators(new ComponentSpanDecorator());
        }

    }

    @Autowired
    private DataSource dataSource;

    @Autowired
    private MockTracer tracer;

    @Test
    void tracesDataSource() throws SQLException {
        try (final Connection connection = dataSource.getConnection();
             final Statement statement = connection.createStatement()) {

            statement.execute("SELECT 1");
        }

        final MockSpan span = getOnlyElement(tracer.finishedSpans());
        final Map<String, Object> tags = span.tags();

        assertThat(tags, aMapWithSize(2));
        assertThat(tags, hasEntry("span.kind", "client"));
        assertThat(tags, hasEntry("component", "JDBC"));
    }

}
