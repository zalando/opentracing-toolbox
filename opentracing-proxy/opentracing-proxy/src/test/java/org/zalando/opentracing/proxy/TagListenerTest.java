package org.zalando.opentracing.proxy;

import io.opentracing.Span;
import io.opentracing.Tracer;
import io.opentracing.mock.MockTracer;
import io.opentracing.tag.StringTag;
import io.opentracing.tag.Tag;
import org.junit.jupiter.api.Test;
import org.mockito.invocation.InvocationOnMock;

import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

class TagListenerTest {

    private final TagListener listener = mock(TagListener.class,
            InvocationOnMock::callRealMethod);

    private final Tracer unit = new ProxyTracer(new MockTracer())
            .with(listener);

    @Test
    void shouldDelegateSpanBuilderStringTag() {
        final Tracer.SpanBuilder builder = unit.buildSpan("test")
                .withTag("k1", "v");

        verify(listener).onTag(eq(builder), tag("k1"), eq("v"));
    }

    @Test
    void shouldDelegateSpanBuilderBooleanTag() {
        final Tracer.SpanBuilder builder = unit.buildSpan("test")
                .withTag("k2", false);

        verify(listener).onTag(eq(builder), tag("k2"), eq(false));
    }

    @Test
    void shouldDelegateSpanBuilderNumberTag() {
        final Tracer.SpanBuilder builder = unit.buildSpan("test")
                .withTag("k3", 0);

        verify(listener).onTag(eq(builder), tag("k3"), eq(0));
    }

    @Test
    void shouldDelegateSpanBuilderTag() {
        final Tracer.SpanBuilder builder = unit.buildSpan("test")
                .withTag(new StringTag("k4"), "v");

        verify(listener).onTag(eq(builder), tag("k4"), eq("v"));
    }

    @Test
    void shouldDelegateSpanStringTag() {
        final Span span = unit.buildSpan("test").start()
                .setTag("k1", "v");

        verify(listener).onTag(eq(span), tag("k1"), eq("v"));
    }

    @Test
    void shouldDelegateSpanBooleanTag() {
        final Span span = unit.buildSpan("test").start()
                .setTag("k2", false);

        verify(listener).onTag(eq(span), tag("k2"), eq(false));
    }

    @Test
    void shouldDelegateSpanNumberTag() {
        final Span span = unit.buildSpan("test").start()
                .setTag("k3", 0);

        verify(listener).onTag(eq(span), tag("k3"), eq(0));
    }

    @Test
    void shouldDelegateSpanTag() {
        final Span span = unit.buildSpan("test").start()
                .setTag(new NumberTag("k4"), 17);

        verify(listener).onTag(eq(span), tag("k4"), eq(17));
    }

    private static <T> Tag<T> tag(final String key) {
        return argThat(tag -> key.equals(tag.getKey()));
    }

}
