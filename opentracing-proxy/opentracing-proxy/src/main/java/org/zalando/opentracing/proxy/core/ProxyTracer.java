package org.zalando.opentracing.proxy.core;

import io.opentracing.Scope;
import io.opentracing.ScopeManager;
import io.opentracing.Span;
import io.opentracing.SpanContext;
import io.opentracing.Tracer;
import io.opentracing.propagation.Format;
import lombok.AllArgsConstructor;
import org.apiguardian.api.API;
import org.zalando.opentracing.proxy.base.ForwardingTracer;
import org.zalando.opentracing.proxy.spi.Plugin;

import javax.annotation.Nullable;

import static lombok.AccessLevel.PRIVATE;
import static org.apiguardian.api.API.Status.EXPERIMENTAL;

@API(status = EXPERIMENTAL)
@AllArgsConstructor(access = PRIVATE)
public final class ProxyTracer implements ForwardingTracer {

    private final Tracer delegate;
    private final PluginRegistry registry;

    public ProxyTracer(final Tracer tracer) {
        this(tracer, new PluginRegistry());
    }

    @Override
    public Tracer delegate() {
        return delegate;
    }

    public ProxyTracer with(final Plugin plugin) {
        return new ProxyTracer(delegate, registry.register(plugin));
    }

    @Override
    public <C> void inject(
            final SpanContext context,
            final Format<C> format,
            final C carrier) {

        registry.interceptors().injections()
                .intercept(ForwardingTracer.super::inject, context, format)
                .inject(context, format, carrier);
    }

    @Override
    public SpanBuilder buildSpan(final String operationName) {
        final SpanBuilder builder = ForwardingTracer.super.buildSpan(
                registry.interceptors().names().rename(operationName));

        return registry.interceptors().spans()
                .intercept(this, new ProxySpanBuilder(this, builder, registry));
    }

    @Override
    public ScopeManager scopeManager() {
        return new ProxyScopeManager(
                ForwardingTracer.super.scopeManager(), registry);
    }

    @Override
    public Scope activateSpan(@Nullable final Span span) {
        if (span == null) {
            return ForwardingTracer.super.activateSpan(null);
        }

        // we rely on the delegate to support any Span here
        final Scope original = ForwardingTracer.super.activateSpan(span);
        final ProxyScope scope = new ProxyScope(original, span, registry);
        registry.listeners().scopes().onActivated(scope, span);
        return scope;
    }

    @Override
    public Span activeSpan() {
        Span span = ForwardingTracer.super.activeSpan();
        if(span == null || span instanceof ProxySpan){
            return span;
        }
        // for opentracing shim, it must still return ProxySpan
        return new ProxySpan(this, span, registry);
    }
}
