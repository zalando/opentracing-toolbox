package org.zalando.opentracing.spring.webflux.extension;

import io.opentracing.contrib.spring.web.webfilter.WebFluxSpanDecorator;
import org.apiguardian.api.API;

import java.util.List;
import java.util.ServiceLoader;

import static java.util.ServiceLoader.load;
import static java.util.stream.Collectors.toList;
import static java.util.stream.StreamSupport.stream;
import static org.apiguardian.api.API.Status.EXPERIMENTAL;
import static org.zalando.opentracing.spring.webflux.extension.CompositeSpanDecorator.composite;

/**
 * @see ServiceLoader
 */
@API(status = EXPERIMENTAL)
public final class ServiceLoaderSpanDecorator
        extends ForwardingSpanDecorator {

    public ServiceLoaderSpanDecorator() {
        super(composite(loadDecorators()));
    }

    private static synchronized List<WebFluxSpanDecorator> loadDecorators() {
        return stream(load(WebFluxSpanDecorator.class).spliterator(), false).collect(toList());
    }

}
