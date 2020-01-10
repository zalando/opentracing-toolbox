package org.zalando.opentracing.proxy.listen.log;

import io.opentracing.Span;
import io.opentracing.Tracer;
import io.opentracing.mock.MockTracer;
import org.junit.jupiter.api.Test;
import org.zalando.opentracing.proxy.core.ProxyTracer;

import java.util.Map;

import static java.util.Collections.singletonMap;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;

class LogListenerTest {

    @SuppressWarnings("Convert2Lambda")
    private final LogListener listener = spy(new LogListener() {
        @Override
        public void onLog(final Span span, final Map<String, ?> fields) {
            // nothing to do
        }
    });

    private final Tracer unit = new ProxyTracer(new MockTracer())
            .with(LogListener.DEFAULT)
            .with(listener);

    @Test
    void shouldDelegateLogString() {
        final Span span = unit.buildSpan("test").start()
                .log("test");

        verify(listener).onLog(span, singletonMap("event", "test"));
    }

    @Test
    void shouldDelegateLogMap() {
        final Span span = unit.buildSpan("test").start()
                .log(singletonMap("k", "v"));

        verify(listener).onLog(span, singletonMap("k", "v"));
    }

    @Test
    void shouldDelegateLogTimestampString() {
        final Span span = unit.buildSpan("test").start()
                .log(0L, "test");

        verify(listener).onLog(span, 0L, singletonMap("event", "test"));
    }

    @Test
    void shouldDelegateLogTimestampMap() {
        final Span span = unit.buildSpan("test").start()
                .log(0L, singletonMap("k", "v"));

        verify(listener).onLog(span, 0L, singletonMap("k", "v"));
    }

}
