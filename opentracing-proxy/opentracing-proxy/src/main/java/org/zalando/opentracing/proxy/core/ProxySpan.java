package org.zalando.opentracing.proxy.core;

import io.opentracing.Span;
import io.opentracing.Tracer;
import io.opentracing.tag.Tag;
import lombok.AllArgsConstructor;
import org.zalando.opentracing.proxy.base.ForwardingSpan;
import org.zalando.opentracing.proxy.intercept.baggage.BaggageInterceptor;
import org.zalando.opentracing.proxy.intercept.log.LogInterceptor;
import org.zalando.opentracing.proxy.intercept.tag.TagInterceptor;
import org.zalando.opentracing.proxy.listen.baggage.BaggageListener;
import org.zalando.opentracing.proxy.listen.log.LogListener;
import org.zalando.opentracing.proxy.listen.span.SpanListener;
import org.zalando.opentracing.proxy.listen.tag.TagListener;

import java.util.Map;

import static java.util.Collections.singletonMap;

@AllArgsConstructor
final class ProxySpan implements ForwardingSpan {

    private final Tracer tracer;
    private final Span delegate;
    private final Plugins plugins;

    @Override
    public Span delegate() {
        return delegate;
    }

    @Override
    public <T> Span setTag(final Tag<T> tag, final T value) {
        final TagInterceptor interceptor = plugins.interceptors().tags();
        final TagListener listener = plugins.listeners().tags();

        interceptor.intercept(tag, value).forEach(pair -> {
            pair.tag(delegate);
            pair.notify(listener, this);
        });

        return this;
    }

    @Override
    public Span log(final Map<String, ?> fields) {
        final LogInterceptor interceptor = plugins.interceptors().logs();
        final LogListener listener = plugins.listeners().logs();

        interceptor.intercept(this, fields).forEach(log -> {
            log.log(delegate);
            log.notify(listener, this);
        });

        return this;
    }

    @Override
    public Span log(final long timestamp, final Map<String, ?> fields) {
        final LogInterceptor interceptor = plugins.interceptors().logs();
        final LogListener listener = plugins.listeners().logs();

        interceptor.intercept(this, timestamp, fields).forEach(log -> {
            log.log(delegate);
            log.notify(listener, this);
        });

        return this;
    }

    @Override
    public Span log(final String event) {
        return log(singletonMap("event", event));
    }

    @Override
    public Span log(final long timestamp, final String event) {
        return log(timestamp, singletonMap("event", event));
    }

    @Override
    public Span setBaggageItem(final String key, final String value) {
        final BaggageInterceptor interceptor = plugins.interceptors().baggage();
        interceptor.intercept(this, key, value).forEach(this::bag);
        return this;
    }

    private void bag(final String key, final String value) {
        final BaggageListener listener = plugins.listeners().baggage();
        ForwardingSpan.super.setBaggageItem(key, value);
        listener.onBaggage(tracer, this, key, value);
    }

    @Override
    public Span setOperationName(final String operationName) {
        return ForwardingSpan.super.setOperationName(
                plugins.interceptors().names().rename(operationName));
    }

    @Override
    public void finish() {
        final SpanListener listener = plugins.listeners().spans();

        listener.onFinishing(this);
        ForwardingSpan.super.finish();
        listener.onFinished(this);
    }

    @Override
    public void finish(final long finishMicros) {
        final SpanListener listener = plugins.listeners().spans();

        listener.onFinishing(this);
        ForwardingSpan.super.finish(finishMicros);
        listener.onFinished(this);
    }

}
