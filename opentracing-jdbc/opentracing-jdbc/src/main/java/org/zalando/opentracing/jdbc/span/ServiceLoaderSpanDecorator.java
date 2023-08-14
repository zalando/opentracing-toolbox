package org.zalando.opentracing.jdbc.span;

import org.apiguardian.api.API;

import java.util.List;
import java.util.ServiceLoader;

import static java.util.ServiceLoader.load;
import static java.util.stream.Collectors.toList;
import static java.util.stream.StreamSupport.stream;
import static org.apiguardian.api.API.Status.EXPERIMENTAL;
import static org.zalando.opentracing.jdbc.span.SpanDecorator.composite;

/**
 * @see ServiceLoader
 */
@API(status = EXPERIMENTAL)
public final class ServiceLoaderSpanDecorator extends ForwardingSpanDecorator {

    public ServiceLoaderSpanDecorator() {
        super(composite(loadDecorators()));
    }

    private static synchronized List<SpanDecorator> loadDecorators() {
        return stream(load(SpanDecorator.class).spliterator(), false).collect(toList());
    }

}
