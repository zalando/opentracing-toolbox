package org.zalando.opentracing.proxy;

import io.opentracing.Span;
import io.opentracing.Tracer;
import io.opentracing.mock.MockTracer;
import org.junit.jupiter.api.Test;
import org.mockito.invocation.InvocationOnMock;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

class SpanListenerTest {

    private final SpanListener listener = mock(SpanListener.class,
            InvocationOnMock::callRealMethod);

    private final Tracer unit = new ProxyTracer(new MockTracer())
            .with(listener);

    @Test
    void shouldDelegateStarted() {
        final Span span = unit.buildSpan("test").start();
        verify(listener).onStarted(span);
    }

    @Test
    void shouldDelegateFinish() {
        final Span span = unit.buildSpan("test").start();
        span.finish();

        verify(listener).onFinishing(span);
        verify(listener).onFinished(span);
    }

    @Test
    void shouldDelegateFinishWithTimestamp() {
        final Span span = unit.buildSpan("test").start();
        span.finish(0L);

        verify(listener).onFinishing(span);
        verify(listener).onFinished(span);
    }

}
