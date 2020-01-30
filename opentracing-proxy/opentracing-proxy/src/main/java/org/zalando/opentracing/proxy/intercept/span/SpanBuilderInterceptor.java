package org.zalando.opentracing.proxy.intercept.span;

import io.opentracing.Tracer;
import io.opentracing.Tracer.SpanBuilder;
import org.apiguardian.api.API;
import org.zalando.opentracing.proxy.spi.Plugin;

import javax.annotation.CheckReturnValue;
import java.util.Arrays;
import java.util.Collection;

import static org.apiguardian.api.API.Status.EXPERIMENTAL;

@API(status = EXPERIMENTAL)
public interface SpanBuilderInterceptor extends Plugin {

    SpanBuilderInterceptor DEFAULT = (tracer, builder) -> builder;

    @CheckReturnValue
    SpanBuilder intercept(Tracer tracer, SpanBuilder builder);

    static SpanBuilderInterceptor composite(
            final SpanBuilderInterceptor... interceptors) {
        return composite(Arrays.asList(interceptors));
    }

    static SpanBuilderInterceptor composite(
            final Collection<SpanBuilderInterceptor> interceptors) {
        return new CompositeSpanBuilderInterceptor(interceptors);
    }

}
