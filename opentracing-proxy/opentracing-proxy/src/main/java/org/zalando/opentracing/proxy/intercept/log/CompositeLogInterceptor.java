package org.zalando.opentracing.proxy.intercept.log;

import io.opentracing.Span;
import lombok.AllArgsConstructor;

import java.util.Collection;
import java.util.Map;

import static java.util.stream.Collectors.toList;

@AllArgsConstructor
final class CompositeLogInterceptor implements LogInterceptor {

    private final Collection<LogInterceptor> interceptors;

    @Override
    public Collection<Log> intercept(
            final Span span,
            final long timestamp,
            final Map<String, ?> fields) {

        return interceptors.stream()
                .map(interceptor ->
                        interceptor.intercept(span, timestamp, fields))
                .flatMap(Collection::stream)
                .collect(toList());
    }

    @Override
    public Collection<Log> intercept(
            final Span span, final Map<String, ?> fields) {

        return interceptors.stream()
                .map(interceptor ->
                        interceptor.intercept(span, fields))
                .flatMap(Collection::stream)
                .collect(toList());
    }

}
