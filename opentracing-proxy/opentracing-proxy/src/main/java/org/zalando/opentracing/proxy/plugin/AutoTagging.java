package org.zalando.opentracing.proxy.plugin;

import io.opentracing.Span;
import io.opentracing.Tracer;
import lombok.AllArgsConstructor;
import org.apiguardian.api.API;
import org.zalando.opentracing.proxy.listen.baggage.BaggageListener;
import org.zalando.opentracing.proxy.listen.span.SpanListener;

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
            final Tracer tracer,
            final Span span,
            final String key,
            final String value) {

        Optional.ofNullable(mapping.get(key))
                .ifPresent(tagKey -> span.setTag(tagKey, value));
    }

}
