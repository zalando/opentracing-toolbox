package org.zalando.opentracing.spring.webflux.extension;

import io.opentracing.contrib.spring.web.webfilter.WebFluxSpanDecorator;
import org.apiguardian.api.API;

import java.util.ServiceLoader;

import static java.util.ServiceLoader.load;
import static org.apiguardian.api.API.Status.EXPERIMENTAL;
import static org.zalando.opentracing.spring.webflux.extension.CompositeSpanDecorator.composite;

/**
 * @see ServiceLoader
 */
@API(status = EXPERIMENTAL)
public final class ServiceLoaderSpanDecorator
        extends ForwardingSpanDecorator {

    public ServiceLoaderSpanDecorator() {
        super(composite(load(WebFluxSpanDecorator.class)));
    }
}
