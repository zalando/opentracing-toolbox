package org.zalando.opentracing.proxy.intercept.injection;

import io.opentracing.SpanContext;
import io.opentracing.propagation.Format;
import org.apiguardian.api.API;
import org.zalando.opentracing.proxy.spi.Plugin;

import javax.annotation.CheckReturnValue;
import java.util.Arrays;

import static org.zalando.opentracing.proxy.intercept.injection.Injection.Injector.NOOP;

@API(status = API.Status.EXPERIMENTAL)
public interface Injection extends Plugin {

    Injection DEFAULT = (injector, context, format) -> injector;
    Injection DISABLE = (injector, context, format) -> NOOP;

    interface Injector {
        Injector NOOP = NoopInjector.NOOP;
        <C> void inject(SpanContext context, Format<C> format, C carrier);
    }

    @CheckReturnValue
    Injector intercept(
            Injector injector,
            SpanContext context,
            Format<?> format);

    static Injection composite(
            final Injection... interceptors) {
        return composite(Arrays.asList(interceptors));
    }

    static Injection composite(
            final Iterable<Injection> interceptors) {
        return new CompositeInjection(interceptors);
    }

}
