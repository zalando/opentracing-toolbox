package org.zalando.opentracing.servlet.extension;

import io.opentracing.contrib.web.servlet.filter.ServletFilterSpanDecorator;
import org.apiguardian.api.API;

import java.util.List;
import java.util.ServiceLoader;

import static java.util.ServiceLoader.load;
import static java.util.stream.Collectors.toList;
import static java.util.stream.StreamSupport.stream;
import static org.apiguardian.api.API.Status.EXPERIMENTAL;

/**
 * @see ServiceLoader
 */
@API(status = EXPERIMENTAL)
public final class ServiceLoaderSpanDecorator extends ForwardingSpanDecorator {

    public ServiceLoaderSpanDecorator() {
        super(CompositeSpanDecorator.composite(loadDecorators()));
    }

    private static synchronized List<ServletFilterSpanDecorator> loadDecorators() {
        return stream(load(ServletFilterSpanDecorator.class).spliterator(), false).collect(toList());
    }

}
