package org.zalando.opentracing.proxy;

import io.opentracing.Scope;
import io.opentracing.ScopeManager;
import io.opentracing.Span;
import io.opentracing.SpanContext;
import io.opentracing.Tracer;
import io.opentracing.propagation.Format;

import javax.annotation.Nullable;

abstract class ForwardingTracer implements Tracer {

    protected abstract Tracer delegate();

    @Override
    public ScopeManager scopeManager() {
        return delegate().scopeManager();
    }

    @Nullable
    @Override
    public Span activeSpan() {
        return delegate().activeSpan();
    }

    @Override
    public Scope activateSpan(final Span span) {
        return delegate().activateSpan(span);
    }

    @Override
    public SpanBuilder buildSpan(final String operationName) {
        return delegate().buildSpan(operationName);
    }

    @Override
    public <C> void inject(final SpanContext context, final Format<C> format, final C carrier) {
        delegate().inject(context, format, carrier);
    }

    @Override
    public <C> SpanContext extract(final Format<C> format, final C carrier) {
        return delegate().extract(format, carrier);
    }

    @Override
    public void close() {
        delegate().close();
    }

}
