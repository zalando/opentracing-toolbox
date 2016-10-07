package org.zalando.tracer;

import org.junit.After;
import org.junit.Test;

import static java.util.Collections.singleton;
import static java.util.Collections.singletonMap;
import static org.mockito.Mockito.mock;

public final class TracerBuilderTest {

    private final TracerFactory.Builder unit = Tracer.builder();

    @After
    public void verifyThatTracerCanBeBuild() {
        unit.build();
    }

    @Test
    public void shouldHandleMultipleTrace() {
        unit.trace("X-Trace-ID");
        unit.trace("X-Request-ID");
    }

    @Test
    public void shouldHandleMultipleTraces() {
        unit.traces(singleton("X-Trace-ID"));
        unit.traces(singleton("X-Request-ID"));
    }

    @Test
    public void shouldHandleMultipleTraceWithGenerator() {
        unit.trace("X-Trace-ID", new UUIDGenerator());
        unit.trace("X-Request-ID", new UUIDGenerator());
    }

    @Test
    public void shouldHandleMultipleTracesWithGenerator() {
        unit.customs(singletonMap("X-Trace-ID", new UUIDGenerator()));
        unit.customs(singletonMap("X-Request-ID", new UUIDGenerator()));
    }

    @Test
    public void shouldHandleMultipleListener() {
        unit.listener(new LoggingTraceListener());
        unit.listener(new MDCTraceListener());
    }

    @Test
    public void shouldHandleMultipleListeners() {
        unit.listeners(singleton(new LoggingTraceListener()));
        unit.listeners(singleton(new MDCTraceListener()));
    }

    @Test
    public void shouldClearTraces() {
        unit.clearTraces();
        unit.trace("X-Foo-ID");
        unit.clearTraces();
    }

    @Test
    public void shouldClearCustoms() {
        unit.clearCustoms();
        unit.customs(singletonMap("X-Foo-ID", () -> "foo"));
        unit.clearCustoms();
    }

    @Test
    public void shouldClearListeners() {
        unit.clearListeners();
        unit.listener(mock(TraceListener.class));
        unit.clearListeners();
    }

}
