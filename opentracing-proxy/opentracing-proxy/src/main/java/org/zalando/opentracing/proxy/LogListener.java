package org.zalando.opentracing.proxy;

import io.opentracing.Span;
import org.apiguardian.api.API;

import java.util.Arrays;
import java.util.Map;

import static java.util.Collections.singletonMap;
import static org.apiguardian.api.API.Status.MAINTAINED;

@API(status = MAINTAINED)
public interface LogListener extends Plugin {

    LogListener DEFAULT = (span, fields) -> {
        // nothing to implement
    };

    default void onLog(
            final Span span,
            final long timestamp,
            final String event) {

        onLog(span, timestamp, singletonMap("event", event));
    }

    default void onLog(
            final Span span,
            final String event) {

        onLog(span, singletonMap("event", event));
    }

    default void onLog(
            final Span span,
            final long timestamp,
            final Map<String, ?> fields) {

        onLog(span, fields);
    }

    void onLog(Span span, Map<String, ?> fields);

    @Override
    default <R extends Registry<R>> R registerTo(final R registry) {
        return registry.withLogListener(this);
    }

    static LogListener composite(final LogListener... listeners) {
        return composite(Arrays.asList(listeners));
    }

    static LogListener composite(final Iterable<LogListener> listeners) {
        return new CompositeLogListener(listeners);
    }

}
