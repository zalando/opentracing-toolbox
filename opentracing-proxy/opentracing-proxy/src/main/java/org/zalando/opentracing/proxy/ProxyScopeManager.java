package org.zalando.opentracing.proxy;

import io.opentracing.Scope;
import io.opentracing.ScopeManager;
import io.opentracing.Span;
import io.opentracing.Tracer;
import lombok.AllArgsConstructor;

import javax.annotation.Nullable;
import java.util.Optional;

import static org.zalando.opentracing.proxy.ProxySpan.unwrap;

@AllArgsConstructor
final class ProxyScopeManager extends ForwardingScopeManager {

    private final Tracer tracer;
    private final ScopeManager delegate;
    private final Options options;

    @Override
    protected ScopeManager delegate() {
        return delegate;
    }

    @Override
    public Scope activate(@Nullable final Span span) {
        if (span == null) {
            return super.activate(null);
        }

        final Scope original = super.activate(unwrap(span));
        final ProxyScope scope = new ProxyScope(original, span, options);
        options.scopes().onActivated(scope, span);
        return scope;
    }

    @Nullable
    @Override
    public Span activeSpan() {
        return Optional.ofNullable(super.activeSpan())
                .map(this::decorate)
                .orElse(null);
    }

    private ProxySpan decorate(final Span span) {
        return new ProxySpan(tracer, span, options);
    }

}
