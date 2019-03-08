package org.zalando.tracer;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class FlowTest {

    @Test
    void shouldUseHeaderName() {
        assertEquals("X-Flow-ID", Flow.Header.FLOW_ID);
    }

    @Test
    void shouldUseBagaggeKey() {
        assertEquals("flow_id", Flow.Baggage.FLOW_ID);
    }

    @Test
    void shouldUseTraceIdLoggingKey() {
        assertEquals("trace_id", Flow.Logging.TRACE_ID);
    }

    @Test
    void shouldUseSpanIdLoggingKey() {
        assertEquals("span_id", Flow.Logging.SPAN_ID);
    }

    @Test
    void shouldUseFlowIdLoggingKey() {
        assertEquals("flow_id", Flow.Logging.FLOW_ID);
    }

}
