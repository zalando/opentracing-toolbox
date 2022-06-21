package org.zalando.opentracing.flowid;

import java.util.HashMap;
import java.util.Map;

import io.opentracing.noop.NoopTracerFactory;
import static org.assertj.core.api.Assertions.*;
import org.junit.jupiter.api.Test;

class DefaultFlowNoopTracerTest {
    private final Flow unit = Flow.create(NoopTracerFactory.create());

    @Test
    void shouldReturnNullOnNoActiveSpan() {
        assertThat(unit.currentSpanId()).isEmpty();
    }

    @Test
    void writeToShouldNotThrow() {
        final Map<String, String> target = new HashMap<>();
        unit.writeTo(target::put);
        assertThat(target).isEmpty();
    }
}
