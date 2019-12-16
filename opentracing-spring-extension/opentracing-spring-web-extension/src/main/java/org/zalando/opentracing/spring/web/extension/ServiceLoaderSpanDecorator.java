package org.zalando.opentracing.spring.web.extension;

import io.opentracing.contrib.spring.web.interceptor.HandlerInterceptorSpanDecorator;
import org.apiguardian.api.API;

import java.util.ServiceLoader;

import static java.util.ServiceLoader.load;
import static org.apiguardian.api.API.Status.EXPERIMENTAL;
import static org.zalando.opentracing.spring.web.extension.CompositeSpanDecorator.composite;

/**
 * @see ServiceLoader
 */
@API(status = EXPERIMENTAL)
public final class ServiceLoaderSpanDecorator
        extends ForwardingSpanDecorator {

    public ServiceLoaderSpanDecorator() {
        super(composite(load(HandlerInterceptorSpanDecorator.class)));
    }
}
