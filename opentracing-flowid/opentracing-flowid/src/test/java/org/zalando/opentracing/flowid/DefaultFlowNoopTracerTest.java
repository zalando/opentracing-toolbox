package org.zalando.opentracing.flowid;

import io.opentracing.noop.NoopTracerFactory;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static java.util.Collections.emptyMap;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class DefaultFlowNoopTracerTest {
    private final Flow unit = Flow.create(NoopTracerFactory.create());

    @Test
    void shouldThrowNoActiveSpanFoundException() {
        assertThrows(IllegalStateException.class, unit::currentId);
    }

    @Test
    void writeToShouldNotThrow() {
        final Map<String, String> target = new HashMap<>();
        unit.writeTo(target::put);
        assertEquals(emptyMap(), target);
    }
}
