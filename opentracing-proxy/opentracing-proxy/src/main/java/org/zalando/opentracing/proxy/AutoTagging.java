package org.zalando.opentracing.proxy;

import io.opentracing.Span;
import lombok.AllArgsConstructor;
import org.apiguardian.api.API;

import java.util.Map;
import java.util.Optional;

import static java.util.Collections.singletonMap;
import static org.apiguardian.api.API.Status.EXPERIMENTAL;

@API(status = EXPERIMENTAL)
@AllArgsConstructor
public final class AutoTagging implements SpanListener, BaggageListener {

    private final Map<String, String> mapping;

    public AutoTagging(final String key) {
        this(key, key);
    }

    public AutoTagging(final String baggageKey, final String tagKey) {
        this(singletonMap(baggageKey, tagKey));
    }

    @Override
    public void onStarted(final Span span) {
        mapping.forEach((baggageKey, tagKey) ->
                Optional.ofNullable(span.getBaggageItem(baggageKey))
                .ifPresent(value -> span.setTag(tagKey, value)));
    }

    @Override
    public void onBaggage(
            final Span span,
            final String key,
            final String value) {

        Optional.ofNullable(mapping.get(key))
                .ifPresent(tagKey -> span.setTag(tagKey, value));
    }

    @Override
    public <R extends Registry<R>> R registerTo(final R registry) {
        return registry
                .withSpanListener(this)
                .withBaggageListener(this);
    }

}
