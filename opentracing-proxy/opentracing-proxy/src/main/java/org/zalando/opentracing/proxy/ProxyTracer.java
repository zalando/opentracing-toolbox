package org.zalando.opentracing.proxy;

import io.opentracing.Scope;
import io.opentracing.ScopeManager;
import io.opentracing.Span;
import io.opentracing.Tracer;
import lombok.AllArgsConstructor;
import org.apiguardian.api.API;

import javax.annotation.Nullable;
import java.util.Optional;

import static lombok.AccessLevel.PRIVATE;
import static org.apiguardian.api.API.Status.EXPERIMENTAL;
import static org.zalando.opentracing.proxy.ProxySpan.unwrap;

@API(status = EXPERIMENTAL)
@AllArgsConstructor(access = PRIVATE)
public final class ProxyTracer extends ForwardingTracer {

    private final Tracer delegate;
    private final Options options;

    public ProxyTracer(final Tracer tracer) {
        this(tracer, new Options());
    }

    @Override
    protected Tracer delegate() {
        return delegate;
    }

    public ProxyTracer with(final Plugin plugin) {
        return new ProxyTracer(delegate, plugin.registerTo(options));
    }

    @Override
    public SpanBuilder buildSpan(final String operationName) {
        final SpanBuilder builder = super.buildSpan(
                options.naming().rename(operationName));

        return options.spanBuilders()
                .intercept(this, new ProxySpanBuilder(builder, options));
    }

    @Override
    public ScopeManager scopeManager() {
        return new ProxyScopeManager(super.scopeManager(), options);
    }

    @Override
    public Scope activateSpan(@Nullable final Span span) {
        if (span == null) {
            return super.activateSpan(null);
        }

        final Scope original = super.activateSpan(unwrap(span));
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
        return new ProxySpan(span, options);
    }

}
