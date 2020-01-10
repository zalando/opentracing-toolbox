package org.zalando.opentracing.proxy.intercept.baggage;

import io.opentracing.Span;
import org.apiguardian.api.API;
import org.zalando.opentracing.proxy.spi.Plugin;

import javax.annotation.CheckReturnValue;
import java.util.Arrays;
import java.util.Collection;
import java.util.Map;

import static java.util.Collections.singletonMap;
import static org.apiguardian.api.API.Status.EXPERIMENTAL;

@API(status = EXPERIMENTAL)
public interface BaggageInterceptor extends Plugin {

    BaggageInterceptor DEFAULT = new BaggageInterceptor() {
        // nothing to do
    };

    @CheckReturnValue
    default Map<String, String> intercept(
            final Span span, final String key, final String value) {
        return singletonMap(key, value);
    }

    static BaggageInterceptor composite(
            final BaggageInterceptor... interceptors) {

        return composite(Arrays.asList(interceptors));
    }

    static BaggageInterceptor composite(
            final Collection<BaggageInterceptor> interceptors) {

        return new CompositeBaggageInterceptor(interceptors);
    }

}
