package org.zalando.opentracing.proxy;

import io.opentracing.Span;
import io.opentracing.SpanContext;
import io.opentracing.Tracer.SpanBuilder;
import io.opentracing.tag.Tag;

import javax.annotation.Nullable;

abstract class ForwardingSpanBuilder implements BaseSpanBuilder {

    protected abstract SpanBuilder delegate();

    @Override
    public SpanBuilder asChildOf(@Nullable final SpanContext parent) {
        delegate().asChildOf(parent);
        return this;
    }

    @Override
    public SpanBuilder asChildOf(@Nullable final Span parent) {
        delegate().asChildOf(parent);
        return this;
    }

    @Override
    public SpanBuilder addReference(final String referenceType, final SpanContext referencedContext) {
        delegate().addReference(referenceType, referencedContext);
        return this;
    }

    @Override
    public SpanBuilder ignoreActiveSpan() {
        delegate().ignoreActiveSpan();
        return this;
    }

    @Override
    public <T> SpanBuilder withTag(final Tag<T> tag, final T value) {
        delegate().withTag(tag, value);
        return this;
    }

    @Override
    public SpanBuilder withStartTimestamp(final long microseconds) {
        delegate().withStartTimestamp(microseconds);
        return this;
    }

    @Override
    public Span start() {
        return delegate().start();
    }

}
