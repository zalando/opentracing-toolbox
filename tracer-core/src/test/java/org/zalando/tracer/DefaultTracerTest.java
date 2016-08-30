package org.zalando.tracer;

import org.junit.Test;

import static java.util.Arrays.asList;

public final class DefaultTracerTest extends AbstractTracerTest {

    private final Tracer tracer = Tracer.builder()
            .traces(asList("X-Trace-ID", "X-Request-ID"))
            .trace("X-Foo-ID", () -> "foo")
            .build();

    @Override
    protected Tracer unit() {
        return tracer;
    }

    @Test(expected = IllegalStateException.class)
    public void shouldFailToStartWithoutProvidedValuesIfAlreadyStarted() {
        tracer.start();
        tracer.start();
    }

    @Test(expected = IllegalStateException.class)
    public void shouldFailToStartWithProvidedValuesIfAlreadyStarted() {
        tracer.start(trace -> "foo");
        tracer.start(trace -> "bar");
    }

}