package org.zalando.opentracing.proxy;

import io.opentracing.Span;
import lombok.AllArgsConstructor;

@AllArgsConstructor
final class CompositeBaggageListener implements BaggageListener {

    private final Iterable<BaggageListener> listeners;

    @Override
    public void onBaggage(
            final Span span,
            final String key,
            final String value) {

        listeners.forEach(listener ->
                listener.onBaggage(span, key, value));
    }

}
