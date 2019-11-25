package org.zalando.opentracing.flowid;

import io.opentracing.noop.NoopTracerFactory;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertThrows;

class DefaultFlowNoopTracerTest {
    private final Flow unit = Flow.create(NoopTracerFactory.create());

    @Test
    void shouldThrowNoActiveSpanFoundException() {
        assertThrows(IllegalStateException.class, unit::currentId);
    }
}
