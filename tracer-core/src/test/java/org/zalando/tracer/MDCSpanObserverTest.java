package org.zalando.tracer;

import io.opentracing.Span;
import io.opentracing.contrib.api.tracer.APIExtensionsTracer;
import io.opentracing.mock.MockTracer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.MDC;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.nullValue;

class MDCSpanObserverTest {

    @BeforeEach
    @AfterEach
    void clear() {
        MDC.clear();
    }

    @Test
    void shouldUpdateMdc() {
        final APIExtensionsTracer tracer = new APIExtensionsTracer(new MockTracer());
        tracer.addTracerObserver(new MDCSpanObserver());

        assertThat(MDC.get("trace_id"), is(nullValue()));
        assertThat(MDC.get("span_id"), is(nullValue()));

        final Span span = tracer.buildSpan("test").start();

        // TODO specify test when actual trace id is available
        assertThat(MDC.get("trace_id"), is(notNullValue()));
        assertThat(MDC.get("span_id"), is(notNullValue()));

        span.finish();

        assertThat(MDC.get("trace_id"), is(nullValue()));
        assertThat(MDC.get("span_id"), is(nullValue()));
    }

}
