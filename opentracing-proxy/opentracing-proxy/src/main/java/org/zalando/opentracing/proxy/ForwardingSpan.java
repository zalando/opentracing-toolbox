package org.zalando.opentracing.proxy;

import io.opentracing.Span;
import io.opentracing.SpanContext;
import io.opentracing.tag.Tag;

import java.util.Map;

abstract class ForwardingSpan implements BaseSpan {

    protected abstract Span delegate();

    @Override
    public SpanContext context() {
        return delegate().context();
    }

    @Override
    public <T> Span setTag(final Tag<T> tag, final T value) {
        delegate().setTag(tag, value);
        return this;
    }

    @Override
    public Span log(final Map<String, ?> fields) {
        delegate().log(fields);
        return this;
    }

    @Override
    public Span log(final long timestampMicroseconds, final Map<String, ?> fields) {
        delegate().log(timestampMicroseconds, fields);
        return this;
    }

    @Override
    public Span log(final String event) {
        delegate().log(event);
        return this;
    }

    @Override
    public Span log(final long timestampMicroseconds, final String event) {
        delegate().log(timestampMicroseconds, event);
        return this;
    }

    @Override
    public Span setBaggageItem(final String key, final String value) {
        delegate().setBaggageItem(key, value);
        return this;
    }

    @Override
    public String getBaggageItem(final String key) {
        return delegate().getBaggageItem(key);
    }

    @Override
    public Span setOperationName(final String operationName) {
        delegate().setOperationName(operationName);
        return this;
    }

    @Override
    public void finish() {
        delegate().finish();
    }

    @Override
    public void finish(final long finishMicros) {
        delegate().finish(finishMicros);
    }

}
