package org.zalando.opentracing.proxy.listen.span;

import io.opentracing.Span;
import lombok.AllArgsConstructor;

@AllArgsConstructor
final class CompositeSpanListener implements SpanListener {

    private final Iterable<SpanListener> listeners;

    @Override
    public void onStarted(final Span span) {
        listeners.forEach(listener ->
                listener.onStarted(span));
    }

    @Override
    public void onFinishing(final Span span) {
        listeners.forEach(listener ->
                listener.onFinishing(span));
    }

    @Override
    public void onFinished(final Span span) {
        listeners.forEach(listener ->
                listener.onFinished(span));
    }

}
