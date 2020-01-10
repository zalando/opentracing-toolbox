package org.zalando.opentracing.proxy.core;

import io.opentracing.Scope;
import io.opentracing.Span;
import lombok.AllArgsConstructor;
import org.zalando.opentracing.proxy.listen.scope.ScopeListener;

@AllArgsConstructor
final class ProxyScope implements Scope {

    private final Scope scope;
    private final Span span;
    private final Plugins plugins;

    @Override
    public void close() {
        final ScopeListener listener = plugins.listeners().scopes();

        try {
            listener.onClosing(this, span);
        } finally {
            scope.close();
        }
        listener.onClosed(this, span);
    }

}
