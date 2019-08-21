package org.zalando.tracer;

import io.opentracing.contrib.api.tracer.APIExtensionsTracer;
import io.opentracing.noop.NoopTracerFactory;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertThrows;

class DefaultFlowNoopTracerTest {
    private final APIExtensionsTracer tracer = new APIExtensionsTracer(NoopTracerFactory.create());
    private final Flow unit = Flow.create(tracer);

    @Test
    void shouldThrowNoActiveSpanFoundException() {
        assertThrows(IllegalStateException.class, unit::currentId);
    }
}
