package org.zalando.opentracing.proxy.autoconfigure;

import com.github.valfirst.slf4jtest.LoggingEvent;
import com.github.valfirst.slf4jtest.TestLogger;
import com.github.valfirst.slf4jtest.TestLoggerFactory;
import com.github.valfirst.slf4jtest.TestLoggerFactoryExtension;
import com.google.common.collect.ImmutableMap;
import io.opentracing.Scope;
import io.opentracing.Span;
import io.opentracing.Tracer;
import io.opentracing.mock.MockSpan;
import io.opentracing.mock.MockTracer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import static com.google.common.collect.Iterables.getOnlyElement;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.aMapWithSize;
import static org.hamcrest.Matchers.hasEntry;
import static org.hamcrest.Matchers.hasKey;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;

@ExtendWith({
        SpringExtension.class,
        TestLoggerFactoryExtension.class
})
@SpringBootTest
@EnableAutoConfiguration
class DefaultConfigurationTest {

    private final TestLogger log = TestLoggerFactory.getTestLogger(DefaultConfigurationTest.class);

    @Configuration
    static class TestConfiguration {

        @Bean
        public MockTracer mockTracer() {
            return new MockTracer();
        }

    }

    @Autowired
    private Tracer tracer;

    @Autowired
    private MockTracer mockTracer;

    @BeforeEach
    void setUp() {
        // TODO dirties context?!
        mockTracer.reset();
    }

    @Test
    void autoTagsFlowId() {
        final String flowId = UUID.randomUUID().toString();

        {
            final Span span = tracer.buildSpan("test").start();
            span.setBaggageItem("flow_id", flowId);
            span.finish();
        }

        final MockSpan span = getOnlyElement(mockTracer.finishedSpans());
        final Map<String, Object> tags = span.tags();
        assertThat(tags, hasEntry("flow_id", flowId));
    }

    @Test
    void correlatesWithTraceSpanAndFlowId() {
        final Span span = tracer.buildSpan("test").start();
        final String flowId = UUID.randomUUID().toString();
        span.setBaggageItem("flow_id", flowId);

        try (final Scope ignored = tracer.activateSpan(span)) {
            log.info("Correlating...");
        } finally {
            span.finish();
        }

        final LoggingEvent event = getOnlyElement(log.getLoggingEvents());
        final ImmutableMap<String, String> mdc = event.getMdc();

        assertThat(mdc, aMapWithSize(3));
        assertThat(mdc, hasEntry("trace_id", span.context().toTraceId()));
        assertThat(mdc, hasEntry("span_id", span.context().toSpanId()));
        assertThat(mdc, hasEntry("flow_id", flowId));
    }

    @Test
    void renamesToLowerUnderscore() {
        tracer.buildSpan("MyTest").start().finish();

        final MockSpan span = getOnlyElement(mockTracer.finishedSpans());
        assertThat(span.operationName(), is("my_test"));
    }

    @Test
    void propagatesFlowId() {
        final Span parent = tracer.buildSpan("parent")
                .withTag("flow_id", UUID.randomUUID().toString())
                .start();

        final Span child = tracer.buildSpan("child")
                .asChildOf(parent)
                .start();

        child.finish();
        parent.finish();

        final List<MockSpan> spans = mockTracer.finishedSpans();

        assertThat(spans, hasSize(2));

        spans.forEach(span ->
                assertThat(span.tags(), hasKey("flow_id")));
    }

}
