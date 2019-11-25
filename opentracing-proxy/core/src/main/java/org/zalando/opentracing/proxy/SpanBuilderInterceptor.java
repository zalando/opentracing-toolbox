package org.zalando.opentracing.proxy;

import io.opentracing.Tracer;
import io.opentracing.Tracer.SpanBuilder;
import org.apiguardian.api.API;

import java.util.Arrays;
import java.util.Collection;

import static org.apiguardian.api.API.Status.EXPERIMENTAL;

@API(status = EXPERIMENTAL)
public interface SpanBuilderInterceptor extends Plugin {

    SpanBuilder intercept(Tracer tracer, SpanBuilder builder);

    @Override
    default <R extends Registry<R>> R registerTo(final R registry) {
        return registry.withSpanBuilderInterceptor(this);
    }

    static SpanBuilderInterceptor composite(
            final SpanBuilderInterceptor... interceptors) {
        return composite(Arrays.asList(interceptors));
    }

    static SpanBuilderInterceptor composite(
            final Collection<SpanBuilderInterceptor> interceptors) {
        return new CompositeSpanBuilderInterceptor(interceptors);
    }

}
