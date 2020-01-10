package org.zalando.opentracing.proxy.intercept.injection;

import io.opentracing.SpanContext;
import io.opentracing.propagation.Format;
import lombok.AllArgsConstructor;

@AllArgsConstructor
final class CompositeInjection implements Injection {

    private final Iterable<Injection> interceptors;

    @Override
    public Injector intercept(
            final Injector injector,
            final SpanContext context,
            final Format<?> format) {

        Injector result = injector;

        for (final Injection interceptor : interceptors) {
            result = interceptor.intercept(result, context, format);
        }

        return result;
    }

}
