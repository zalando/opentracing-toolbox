package org.zalando.opentracing.proxy.listen.log;

import io.opentracing.Span;
import org.apiguardian.api.API;
import org.zalando.opentracing.proxy.spi.Plugin;

import java.util.Arrays;
import java.util.Map;

import static org.apiguardian.api.API.Status.MAINTAINED;

@API(status = MAINTAINED)
public interface LogListener extends Plugin {

    LogListener DEFAULT = (span, fields) -> {
        // nothing to implement
    };

    default void onLog(
            final Span span,
            final long timestamp,
            final Map<String, ?> fields) {

        onLog(span, fields);
    }

    void onLog(Span span, Map<String, ?> fields);

    static LogListener composite(final LogListener... listeners) {
        return composite(Arrays.asList(listeners));
    }

    static LogListener composite(final Iterable<LogListener> listeners) {
        return new CompositeLogListener(listeners);
    }

}
