package org.zalando.opentracing.flowid;

import io.opentracing.Scope;
import io.opentracing.Span;
import io.opentracing.mock.MockSpan;
import io.opentracing.noop.NoopTracer;
import io.opentracing.noop.NoopTracerFactory;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import static java.util.Collections.emptyMap;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class DefaultFlowNoopTracerTest {
    NoopTracer tracer = NoopTracerFactory.create();
    private final Flow unit = Flow.create(tracer);

    @Test
    void shouldThrowNoActiveSpanFoundException() {
        assertThrows(IllegalStateException.class, unit::currentId);
    }

    @Test
    void writeToShouldNotThrow() {
        final Span span = tracer.buildSpan("test").start();

        try (final Scope ignored = tracer.activateSpan(span)) {
            final Map<String, String> target = new HashMap<>();

            unit.writeTo(target::put);

            assertEquals(emptyMap(), target);
        }
    }
}
