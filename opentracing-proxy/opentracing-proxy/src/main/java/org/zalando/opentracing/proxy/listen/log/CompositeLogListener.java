package org.zalando.opentracing.proxy.listen.log;

import io.opentracing.Span;
import lombok.AllArgsConstructor;

import java.util.Map;

@AllArgsConstructor
final class CompositeLogListener implements LogListener {

    private final Iterable<LogListener> listeners;

    @Override
    public void onLog(final Span span, final long timestamp, final Map<String, ?> fields) {
        listeners.forEach(listener ->
                listener.onLog(span, timestamp, fields));
    }

    @Override
    public void onLog(final Span span, final Map<String, ?> fields) {
        listeners.forEach(listener ->
                listener.onLog(span, fields));
    }

}
