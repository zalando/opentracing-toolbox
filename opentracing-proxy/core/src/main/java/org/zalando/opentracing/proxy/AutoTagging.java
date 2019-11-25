package org.zalando.opentracing.proxy;

import io.opentracing.Span;
import lombok.AllArgsConstructor;
import org.apiguardian.api.API;

import java.util.Optional;

import static org.apiguardian.api.API.Status.EXPERIMENTAL;

@API(status = EXPERIMENTAL)
@AllArgsConstructor
public final class AutoTagging implements SpanListener, BaggageListener {

    private final String baggageKey;
    private final String tagKey;

    public AutoTagging(final String key) {
        this(key, key);
    }

    @Override
    public void onStarted(final Span span) {
        Optional.ofNullable(span.getBaggageItem(baggageKey))
                .ifPresent(value -> tag(span, value));
    }

    @Override
    public void onBaggage(
            final Span span,
            final String key,
            final String value) {

        if (baggageKey.equals(key)) {
            tag(span, value);
        }
    }

    private void tag(final Span span, final String baggageItem) {
        span.setTag(tagKey, baggageItem);
    }

    @Override
    public <R extends Registry<R>> R registerTo(final R registry) {
        return registry
                .withSpanListener(this)
                .withBaggageListener(this);
    }

}
