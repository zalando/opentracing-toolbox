package org.zalando.tracer;

import io.opentracing.Span;
import io.opentracing.contrib.api.tracer.APIExtensionsTracer;
import io.opentracing.mock.MockTracer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.MDC;
import org.zalando.tracer.Flow.Baggage;

import static java.util.Collections.singletonMap;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;
import static org.zalando.tracer.Flow.Logging.FLOW_ID;
import static org.zalando.tracer.Flow.Logging.SPAN_ID;
import static org.zalando.tracer.Flow.Logging.TRACE_ID;

class MDCSpanObserverTest {
    
    private final APIExtensionsTracer tracer = new APIExtensionsTracer(new MockTracer());

    MDCSpanObserverTest() {
        tracer.addTracerObserver(new MDCSpanObserver());
    }

    @BeforeEach
    @AfterEach
    void clear() {
        MDC.clear();
    }

    @Test
    void shouldPutTraceAndSpanIdToMdc() {
        assertThat(MDC.get(TRACE_ID), is(nullValue()));
        assertThat(MDC.get(SPAN_ID), is(nullValue()));

        final Span span = tracer.buildSpan("test").start()
                .setOperationName("test")
                .setTag("key", "value")
                .log("event")
                .log(singletonMap("key", "value"));

        assertThat(MDC.get(TRACE_ID), is(span.context().toTraceId()));
        assertThat(MDC.get(SPAN_ID), is(span.context().toSpanId()));

        span.finish();

        assertThat(MDC.get(TRACE_ID), is(nullValue()));
        assertThat(MDC.get(SPAN_ID), is(nullValue()));
    }
    
    @Test
    void shoulPutFlowIdToMdc() {
        assertThat(MDC.get(FLOW_ID), is(nullValue()));

        final Span span = tracer.buildSpan("test").start()
                .setBaggageItem(Baggage.FLOW_ID, "REcCvlqMSReeo7adheiYFA");

        assertThat(MDC.get(FLOW_ID), is("REcCvlqMSReeo7adheiYFA"));

        span.finish();
    }

    @Test
    void shouldIgnoreOtherBaggageItems() {
        assertThat(MDC.get("test"), is(nullValue()));

        final Span span = tracer.buildSpan("test").start()
                .setBaggageItem("test", "test");

        assertThat(MDC.get("test"), is(nullValue()));

        span.finish();
    }

}
