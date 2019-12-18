package org.zalando.opentracing.proxy;

import io.opentracing.Span;
import org.apiguardian.api.API;

import java.util.Arrays;

import static org.apiguardian.api.API.Status.MAINTAINED;

@API(status = MAINTAINED)
public interface BaggageListener extends Plugin {

    BaggageListener DEFAULT = (span, key, value) -> {
        // nothing to implement
    };

    void onBaggage(Span span, String key, String value);

    @Override
    default <R extends Registry<R>> R registerTo(final R registry) {
        return registry.withBaggageListener(this);
    }

    static BaggageListener baggage(final BaggageListener listener) {
        return listener;
    }

    static BaggageListener composite(final BaggageListener... listeners) {
        return composite(Arrays.asList(listeners));
    }

    static BaggageListener composite(final Iterable<BaggageListener> listeners) {
        return new CompositeBaggageListener(listeners);
    }

}
