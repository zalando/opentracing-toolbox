package org.zalando.opentracing.proxy.core;

import io.opentracing.Span;
import io.opentracing.Tracer;
import io.opentracing.Tracer.SpanBuilder;
import io.opentracing.tag.Tag;
import lombok.AllArgsConstructor;
import org.zalando.opentracing.proxy.base.ForwardingSpanBuilder;
import org.zalando.opentracing.proxy.intercept.tag.TagInterceptor;
import org.zalando.opentracing.proxy.listen.tag.TagListener;

import javax.annotation.Nullable;

import static org.zalando.opentracing.proxy.base.ForwardingSpan.unwrap;

@AllArgsConstructor
final class ProxySpanBuilder implements ForwardingSpanBuilder {

    private final Tracer tracer;
    private final SpanBuilder delegate;
    private final Plugins plugins;

    @Override
    public SpanBuilder delegate() {
        return delegate;
    }

    @Override
    public SpanBuilder asChildOf(@Nullable final Span parent) {
        if (parent == null) {
            return this;
        }

        return ForwardingSpanBuilder.super.asChildOf(unwrap(parent));
    }

    @Override
    public <T> SpanBuilder withTag(final Tag<T> tag, final T value) {
        final TagInterceptor interceptor = plugins.interceptors().tags();
        final TagListener listener = plugins.listeners().tags();

        interceptor.intercept(tag, value).forEach(pair -> {
            pair.tag(delegate);
            pair.notify(listener, this);
        });

        return this;
    }

    @Override
    public Span start() {
        final ProxySpan span = new ProxySpan(tracer, delegate.start(), plugins);
        plugins.listeners().spans().onStarted(span);
        return span;
    }

}
