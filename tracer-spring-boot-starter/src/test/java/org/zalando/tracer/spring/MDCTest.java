package org.zalando.tracer.spring;

import io.opentracing.Span;
import io.opentracing.Tracer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = Application.class)
@ImportAutoConfiguration(TracerAutoConfiguration.class)
final class MDCTest {

    @Autowired
    private Tracer tracer;

    @BeforeEach
    @AfterEach
    void clear() {
        MDC.clear();
    }

    @Test
    void shouldEnableMdcByDefault() {
        assertThat(MDC.get("trace_id"), is(nullValue()));
        assertThat(MDC.get("span_id"), is(nullValue()));

        final Span span = tracer.buildSpan("test").start();

        assertThat(MDC.get("trace_id"), is(span.context().toTraceId()));
        assertThat(MDC.get("span_id"), is(span.context().toSpanId()));

        span.finish();

        assertThat(MDC.get("trace_id"), is(nullValue()));
        assertThat(MDC.get("span_id"), is(nullValue()));
    }

}
