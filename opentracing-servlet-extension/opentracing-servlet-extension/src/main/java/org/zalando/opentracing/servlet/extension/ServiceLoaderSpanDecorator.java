package org.zalando.opentracing.servlet.extension;

import io.opentracing.contrib.web.servlet.filter.ServletFilterSpanDecorator;
import org.apiguardian.api.API;

import java.util.ServiceLoader;

import static java.util.ServiceLoader.load;
import static org.apiguardian.api.API.Status.EXPERIMENTAL;

/**
 * @see ServiceLoader
 */
@API(status = EXPERIMENTAL)
public final class ServiceLoaderSpanDecorator extends ForwardingSpanDecorator {

    public ServiceLoaderSpanDecorator() {
        super(CompositeSpanDecorator.composite(load(ServletFilterSpanDecorator.class)));
    }

}
