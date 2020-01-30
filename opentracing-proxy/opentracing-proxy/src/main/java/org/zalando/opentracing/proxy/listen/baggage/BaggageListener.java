package org.zalando.opentracing.proxy.listen.baggage;

import io.opentracing.Span;
import io.opentracing.Tracer;
import org.apiguardian.api.API;
import org.zalando.opentracing.proxy.spi.Plugin;

import java.util.Arrays;

import static org.apiguardian.api.API.Status.MAINTAINED;

@API(status = MAINTAINED)
public interface BaggageListener extends Plugin {

    BaggageListener DEFAULT = (tracer, span, key, value) -> {
        // nothing to implement
    };

    void onBaggage(Tracer tracer, Span span, String key, String value);

    static BaggageListener composite(final BaggageListener... listeners) {
        return composite(Arrays.asList(listeners));
    }

    static BaggageListener composite(final Iterable<BaggageListener> listeners) {
        return new CompositeBaggageListener(listeners);
    }

}
