package org.zalando.opentracing.proxy.core;

import io.opentracing.Scope;
import io.opentracing.ScopeManager;
import io.opentracing.Span;
import lombok.AllArgsConstructor;
import org.zalando.opentracing.proxy.base.ForwardingScopeManager;

import javax.annotation.Nullable;

@AllArgsConstructor
final class ProxyScopeManager implements ForwardingScopeManager {

    private final ScopeManager delegate;
    private final Plugins plugins;

    @Override
    public ScopeManager delegate() {
        return delegate;
    }

    @Override
    public Scope activate(@Nullable final Span span) {
        if (span == null) {
            return ForwardingScopeManager.super.activate(null);
        }

        // we rely on the delegate to support any Span here
        final Scope original = ForwardingScopeManager.super.activate(span);
        final ProxyScope scope = new ProxyScope(original, span, plugins);
        plugins.listeners().scopes().onActivated(scope, span);
        return scope;
    }

}
