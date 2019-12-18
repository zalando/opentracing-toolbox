package org.zalando.opentracing.proxy;

import io.opentracing.SpanContext;
import io.opentracing.propagation.Format;
import org.apiguardian.api.API;

import java.util.Arrays;

import static org.zalando.opentracing.proxy.Injection.Injector.NOOP;

@API(status = API.Status.EXPERIMENTAL)
public interface Injection extends Plugin {

    Injection DEFAULT = (injector, context, format) -> injector;
    Injection DISABLE = (injector, context, format) -> NOOP;

    interface Injector {
        Injector NOOP = NoopInjector.NOOP;
        <C> void inject(SpanContext context, Format<C> format, C carrier);
    }

    Injector intercept(
            Injector injector,
            SpanContext context,
            Format<?> format);

    @Override
    default <R extends Registry<R>> R registerTo(final R registry) {
        return registry.withInjection(this);
    }

    static Injection composite(
            final Injection... interceptors) {
        return composite(Arrays.asList(interceptors));
    }

    static Injection composite(
            final Iterable<Injection> interceptors) {
        return new CompositeInjection(interceptors);
    }

}
