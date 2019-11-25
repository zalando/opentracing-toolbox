package org.zalando.opentracing.proxy;

import io.opentracing.Span;
import io.opentracing.Tracer.SpanBuilder;
import io.opentracing.tag.Tag;
import lombok.AllArgsConstructor;

import javax.annotation.Nullable;

import static org.zalando.opentracing.proxy.ProxySpan.unwrap;

@AllArgsConstructor
final class ProxySpanBuilder extends ForwardingSpanBuilder {

    private final SpanBuilder delegate;
    private final Options options;

    @Override
    protected SpanBuilder delegate() {
        return delegate;
    }

    @Override
    public SpanBuilder asChildOf(@Nullable final Span parent) {
        if (parent == null) {
            return this;
        }

        return super.asChildOf(unwrap(parent));
    }

    @Override
    public <T> SpanBuilder withTag(final Tag<T> tag, final T value) {
        final SpanBuilder builder = super.withTag(tag, value);
        options.tags().onTag(builder, tag, value);
        return builder;
    }

    @Override
    public Span start() {
        final ProxySpan span = new ProxySpan(super.start(), options);
        options.spans().onStarted(span);
        return span;
    }

}
