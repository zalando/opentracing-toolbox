package org.zalando.opentracing.proxy.intercept.span;

import io.opentracing.Tracer;
import io.opentracing.Tracer.SpanBuilder;
import lombok.AllArgsConstructor;

import java.util.Collection;

@AllArgsConstructor
final class CompositeSpanBuilderInterceptor implements SpanBuilderInterceptor {

    private final Collection<SpanBuilderInterceptor> interceptors;

    @Override
    public SpanBuilder intercept(final Tracer tracer, final SpanBuilder builder) {
        SpanBuilder result = builder;

        for (final SpanBuilderInterceptor interceptor : interceptors) {
            result = interceptor.intercept(tracer, result);
        }

        return result;
    }
}
