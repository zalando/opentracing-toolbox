package org.zalando.opentracing.proxy.listen.baggage;

import io.opentracing.Span;
import io.opentracing.Tracer;
import lombok.AllArgsConstructor;

@AllArgsConstructor
final class CompositeBaggageListener implements BaggageListener {

    private final Iterable<BaggageListener> listeners;

    @Override
    public void onBaggage(
            final Tracer tracer,
            final Span span,
            final String key,
            final String value) {

        listeners.forEach(listener ->
                listener.onBaggage(tracer, span, key, value));
    }

}
