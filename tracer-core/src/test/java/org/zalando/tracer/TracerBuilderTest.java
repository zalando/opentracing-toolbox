package org.zalando.tracer;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import static java.util.Collections.singleton;
import static java.util.Collections.singletonMap;
import static org.mockito.Mockito.mock;

final class TracerBuilderTest {

    private final TracerFactory.Builder unit = Tracer.builder();

    @AfterEach
    void verifyThatTracerCanBeBuild() {
        unit.build();
    }

    @Test
    void shouldHandleMultipleTrace() {
        unit.trace("X-Trace-ID");
        unit.trace("X-Request-ID");
    }

    @Test
    void shouldHandleMultipleTraces() {
        unit.traces(singleton("X-Trace-ID"));
        unit.traces(singleton("X-Request-ID"));
    }

    @Test
    void shouldHandleMultipleTraceWithGenerator() {
        unit.trace("X-Trace-ID", new UUIDGenerator());
        unit.trace("X-Request-ID", new UUIDGenerator());
    }

    @Test
    void shouldHandleMultipleTracesWithGenerator() {
        unit.customs(singletonMap("X-Trace-ID", new UUIDGenerator()));
        unit.customs(singletonMap("X-Request-ID", new UUIDGenerator()));
    }

    @Test
    void shouldHandleMultipleListener() {
        unit.listener(new LoggingTraceListener());
        unit.listener(new MDCTraceListener());
    }

    @Test
    void shouldHandleMultipleListeners() {
        unit.listeners(singleton(new LoggingTraceListener()));
        unit.listeners(singleton(new MDCTraceListener()));
    }

    @Test
    void shouldClearTraces() {
        unit.clearTraces();
        unit.trace("X-Foo-ID");
        unit.clearTraces();
    }

    @Test
    void shouldClearCustoms() {
        unit.clearCustoms();
        unit.customs(singletonMap("X-Foo-ID", () -> "foo"));
        unit.clearCustoms();
    }

    @Test
    void shouldClearListeners() {
        unit.clearListeners();
        unit.listener(mock(TraceListener.class));
        unit.clearListeners();
    }

}
