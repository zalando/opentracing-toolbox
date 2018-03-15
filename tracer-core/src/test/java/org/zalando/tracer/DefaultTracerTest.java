package org.zalando.tracer;

import org.junit.jupiter.api.Test;

import static java.util.Arrays.asList;
import static org.junit.jupiter.api.Assertions.assertThrows;

final class DefaultTracerTest extends AbstractTracerTest {

    private final Tracer tracer = Tracer.builder()
            .traces(asList("X-Trace-ID", "X-Request-ID"))
            .trace("X-Foo-ID", () -> "foo")
            .build();

    @Override
    protected Tracer unit() {
        return tracer;
    }

    @Test
    void shouldFailToStartWithoutProvidedValuesIfAlreadyStarted() {
        assertThrows(IllegalStateException.class, () -> {
            tracer.start();
            tracer.start();
        });
    }

    @Test
    void shouldFailToStartWithProvidedValuesIfAlreadyStarted() {
        assertThrows(IllegalStateException.class, () -> {
            tracer.start(trace -> "foo");
            tracer.start(trace -> "bar");
        });
    }

}
