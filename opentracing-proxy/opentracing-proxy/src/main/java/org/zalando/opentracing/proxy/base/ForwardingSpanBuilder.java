package org.zalando.opentracing.proxy.base;

import io.opentracing.Span;
import io.opentracing.SpanContext;
import io.opentracing.Tracer.SpanBuilder;
import io.opentracing.tag.Tag;
import org.apiguardian.api.API;

import javax.annotation.Nullable;

import static org.apiguardian.api.API.Status.EXPERIMENTAL;

@API(status = EXPERIMENTAL)
public interface ForwardingSpanBuilder extends NormalizingSpanBuilder {

    SpanBuilder delegate();

    @Override
    default SpanBuilder asChildOf(@Nullable final SpanContext parent) {
        delegate().asChildOf(parent);
        return this;
    }

    @Override
    default SpanBuilder asChildOf(@Nullable final Span parent) {
        delegate().asChildOf(parent);
        return this;
    }

    @Override
    default SpanBuilder addReference(final String referenceType, final SpanContext referencedContext) {
        delegate().addReference(referenceType, referencedContext);
        return this;
    }

    @Override
    default SpanBuilder ignoreActiveSpan() {
        delegate().ignoreActiveSpan();
        return this;
    }

    @Override
    default <T> SpanBuilder withTag(final Tag<T> tag, final T value) {
        delegate().withTag(tag, value);
        return this;
    }

    @Override
    default SpanBuilder withStartTimestamp(final long microseconds) {
        delegate().withStartTimestamp(microseconds);
        return this;
    }

    @Override
    default Span start() {
        return delegate().start();
    }

}
