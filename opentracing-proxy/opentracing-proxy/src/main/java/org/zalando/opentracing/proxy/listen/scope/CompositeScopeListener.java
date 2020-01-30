package org.zalando.opentracing.proxy.listen.scope;

import io.opentracing.Scope;
import io.opentracing.Span;
import lombok.AllArgsConstructor;

@AllArgsConstructor
final class CompositeScopeListener implements ScopeListener {

    private final Iterable<ScopeListener> listeners;

    @Override
    public void onActivated(final Scope scope, final Span span) {
        listeners.forEach(listener ->
                listener.onActivated(scope, span));
    }

    @Override
    public void onClosing(final Scope scope, final Span span) {
        listeners.forEach(listener ->
                listener.onClosing(scope, span));
    }

    @Override
    public void onClosed(final Scope scope, final Span span) {
        listeners.forEach(listener ->
                listener.onClosed(scope, span));
    }

}
