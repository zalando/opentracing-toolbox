package org.zalando.opentracing.proxy;

import io.opentracing.Span;
import org.apiguardian.api.API;

import java.util.Arrays;

import static org.apiguardian.api.API.Status.MAINTAINED;

@API(status = MAINTAINED)
public interface SpanListener extends Plugin {

    default void onStarted(final Span span) {
        // nothing to do
    }

    default void onFinishing(final Span span) {
        // nothing to do
    }

    default void onFinished(final Span span) {
        // nothing to do
    }

    @Override
    default <R extends Registry<R>> R registerTo(final R registry) {
        return registry.withSpanListener(this);
    }

    static SpanListener composite(final SpanListener... listeners) {
        return composite(Arrays.asList(listeners));
    }

    static SpanListener composite(final Iterable<SpanListener> listeners) {
        return new CompositeSpanListener(listeners);
    }

}
