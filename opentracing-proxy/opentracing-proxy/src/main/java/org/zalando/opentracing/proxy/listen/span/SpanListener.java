package org.zalando.opentracing.proxy.listen.span;

import io.opentracing.Span;
import org.apiguardian.api.API;
import org.zalando.opentracing.proxy.spi.Plugin;

import java.util.Arrays;

import static org.apiguardian.api.API.Status.MAINTAINED;

@API(status = MAINTAINED)
public interface SpanListener extends Plugin {

    SpanListener DEFAULT = new SpanListener() {
        // nothing to implement
    };

    default void onStarted(final Span span) {
        // nothing to do
    }

    default void onFinishing(final Span span) {
        // nothing to do
    }

    default void onFinished(final Span span) {
        // nothing to do
    }

    static SpanListener composite(final SpanListener... listeners) {
        return composite(Arrays.asList(listeners));
    }

    static SpanListener composite(final Iterable<SpanListener> listeners) {
        return new CompositeSpanListener(listeners);
    }

}
