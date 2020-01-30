package org.zalando.opentracing.proxy.base;

import io.opentracing.Scope;
import io.opentracing.ScopeManager;
import io.opentracing.Span;
import io.opentracing.SpanContext;
import io.opentracing.Tracer;
import io.opentracing.propagation.Format;
import org.apiguardian.api.API;

import static org.apiguardian.api.API.Status.EXPERIMENTAL;

@API(status = EXPERIMENTAL)
public interface ForwardingTracer extends Tracer {

    Tracer delegate();

    @Override
    default ScopeManager scopeManager() {
        return delegate().scopeManager();
    }

    @Override
    default Span activeSpan() {
        return delegate().activeSpan();
    }

    @Override
    default Scope activateSpan(final Span span) {
        return delegate().activateSpan(span);
    }

    @Override
    default SpanBuilder buildSpan(final String operationName) {
        return delegate().buildSpan(operationName);
    }

    @Override
    default <C> void inject(final SpanContext context, final Format<C> format, final C carrier) {
        delegate().inject(context, format, carrier);
    }

    @Override
    default <C> SpanContext extract(final Format<C> format, final C carrier) {
        return delegate().extract(format, carrier);
    }

    @Override
    default void close() {
        delegate().close();
    }

}
