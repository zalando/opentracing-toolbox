package org.zalando.opentracing.proxy.base;

import io.opentracing.Span;
import io.opentracing.SpanContext;
import io.opentracing.tag.Tag;
import org.apiguardian.api.API;

import javax.annotation.Nullable;
import java.util.Map;
import java.util.Optional;

import static org.apiguardian.api.API.Status.EXPERIMENTAL;

@API(status = EXPERIMENTAL)
public interface ForwardingSpan extends NormalizingSpan {

    Span delegate();

    @Override
    default SpanContext context() {
        return delegate().context();
    }

    @Override
    default  <T> Span setTag(final Tag<T> tag, final T value) {
        delegate().setTag(tag, value);
        return this;
    }

    @Override
    default Span log(final Map<String, ?> fields) {
        delegate().log(fields);
        return this;
    }

    @Override
    default Span log(final long timestampMicroseconds, final Map<String, ?> fields) {
        delegate().log(timestampMicroseconds, fields);
        return this;
    }

    @Override
    default Span log(final String event) {
        delegate().log(event);
        return this;
    }

    @Override
    default Span log(final long timestampMicroseconds, final String event) {
        delegate().log(timestampMicroseconds, event);
        return this;
    }

    @Override
    default Span setBaggageItem(final String key, final String value) {
        delegate().setBaggageItem(key, value);
        return this;
    }

    @Override
    default String getBaggageItem(final String key) {
        return delegate().getBaggageItem(key);
    }

    @Override
    default Span setOperationName(final String operationName) {
        delegate().setOperationName(operationName);
        return this;
    }

    @Override
    default void finish() {
        delegate().finish();
    }

    @Override
    default void finish(final long finishMicros) {
        delegate().finish(finishMicros);
    }

    static Span unwrap(final Span span) {
        return Optional.of(span)
                .filter(ForwardingSpan.class::isInstance)
                .map(ForwardingSpan.class::cast)
                .map(ForwardingSpan::delegate)
                .map(ForwardingSpan::unwrap) // recursion
                .orElse(span);
    }

    static <S> Optional<S> unwrap(@Nullable final Span span, final Class<S> type) {
        if (type.isInstance(span)) {
            return Optional.of(type.cast(span));
        }

        return Optional.ofNullable(span)
                .filter(ForwardingSpan.class::isInstance)
                .map(ForwardingSpan.class::cast)
                .map(ForwardingSpan::delegate)
                .flatMap(s -> unwrap(s, type));
    }

}
