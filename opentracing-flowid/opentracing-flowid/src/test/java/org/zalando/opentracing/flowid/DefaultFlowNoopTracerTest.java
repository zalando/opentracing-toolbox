package org.zalando.opentracing.flowid;

import java.util.HashMap;
import java.util.Map;

import io.opentracing.noop.NoopTracerFactory;
import static java.util.Collections.*;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

class DefaultFlowNoopTracerTest {
    private final Flow unit = Flow.create(NoopTracerFactory.create());

    @Test
    void shouldReturnNullOnNoActiveSpan() {
        assertNull(unit.currentId());
    }

    @Test
    void writeToShouldNotThrow() {
        final Map<String, String> target = new HashMap<>();
        unit.writeTo(target::put);
        assertEquals(emptyMap(), target);
    }
}
