package org.zalando.opentracing.proxy;

import io.opentracing.Span;
import io.opentracing.Tracer;
import io.opentracing.tag.Tag;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;

import java.util.Map;
import java.util.Optional;

@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = false)
final class ProxySpan extends ForwardingSpan {

    private final Tracer tracer;

    @EqualsAndHashCode.Include
    private final Span delegate;
    private final Options options;

    @Override
    protected Span delegate() {
        return delegate;
    }

    @Override
    public <T> Span setTag(final Tag<T> tag, final T value) {
        super.setTag(tag, value);
        options.tags().onTag(this, tag, value);
        return this;
    }

    @Override
    public Span log(final Map<String, ?> fields) {
        super.log(fields);
        options.logs().onLog(this, fields);
        return this;
    }

    @Override
    public Span log(final long timestamp, final Map<String, ?> fields) {
        super.log(timestamp, fields);
        options.logs().onLog(this, timestamp, fields);
        return this;
    }

    @Override
    public Span log(final String event) {
        super.log(event);
        options.logs().onLog(this, event);
        return this;
    }

    @Override
    public Span log(final long timestamp, final String event) {
        super.log(timestamp, event);
        options.logs().onLog(this, timestamp, event);
        return this;
    }

    @Override
    public Span setBaggageItem(final String key, final String value) {
        super.setBaggageItem(key, value);
        options.baggage().onBaggage(tracer, this, key, value);
        return this;
    }

    @Override
    public Span setOperationName(final String operationName) {
        return super.setOperationName(options.naming().rename(operationName));
    }

    @Override
    public void finish() {
        options.spans().onFinishing(this);
        super.finish();
        options.spans().onFinished(this);
    }

    @Override
    public void finish(final long finishMicros) {
        options.spans().onFinishing(this);
        super.finish(finishMicros);
        options.spans().onFinished(this);
    }

    static Span unwrap(final Span span) {
        return Optional.of(span)
                .filter(ProxySpan.class::isInstance)
                .map(ProxySpan.class::cast)
                .map(ProxySpan::delegate)
                .map(ProxySpan::unwrap) // recursive call
                .orElse(span);
    }

}
