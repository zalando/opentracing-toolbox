package org.zalando.opentracing.jdbc.autoconfigure;

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
import org.zalando.opentracing.jdbc.Activation;
import org.zalando.opentracing.jdbc.ActiveSpanLifecycle;
import org.zalando.opentracing.jdbc.DefaultActivation;
import org.zalando.opentracing.jdbc.Lifecycle;
import org.zalando.opentracing.jdbc.NewSpanLifecycle;
import org.zalando.opentracing.jdbc.operation.OperationName;
import org.zalando.opentracing.jdbc.span.SpanDecorator;
import org.zalando.opentracing.jdbc.span.StaticSpanDecorator;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Map;

import static java.util.Collections.singletonMap;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.aMapWithSize;
import static org.hamcrest.Matchers.hasEntry;
import static org.hamcrest.Matchers.is;
import static org.junit.platform.commons.util.CollectionUtils.getOnlyElement;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.NONE;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = NONE)
@EnableAutoConfiguration
class CustomConfigurationTest {

    @Configuration
    static class TestConfiguration {

        @Bean
        public MockTracer tracer() {
            return new MockTracer();
        }

        @Bean
        public OperationName operationName() {
            return (method, queries) -> method.getName().toUpperCase();
        }

        @Bean
        public Lifecycle lifecycle() {
            return Lifecycle.composite(
                    new ActiveSpanLifecycle(),
                    new NewSpanLifecycle()
            );
        }

        @Bean
        public Activation activation() {
            return new DefaultActivation();
        }

        @Bean
        public SpanDecorator staticSpanDecorator() {
            return new StaticSpanDecorator(singletonMap("test", "true"));
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

        assertThat(span.operationName(), is("EXECUTE"));
        assertThat(tags, aMapWithSize(2));
        assertThat(tags, hasEntry("span.kind", "client"));
        assertThat(tags, hasEntry("test", "true"));
    }

}
