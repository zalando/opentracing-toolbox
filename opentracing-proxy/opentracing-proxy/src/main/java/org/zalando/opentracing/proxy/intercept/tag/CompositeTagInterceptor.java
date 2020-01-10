package org.zalando.opentracing.proxy.intercept.tag;

import io.opentracing.tag.Tag;
import lombok.AllArgsConstructor;

import java.util.Collection;

import static java.util.stream.Collectors.toList;

@AllArgsConstructor
final class CompositeTagInterceptor implements TagInterceptor {

    private final Collection<TagInterceptor> interceptors;

    @Override
    public <T> Collection<TagPair> intercept(
            final Tag<T> tag, final T value) {

        return interceptors.stream()
                .map(interceptor -> interceptor.intercept(tag, value))
                .flatMap(Collection::stream)
                .collect(toList());
    }

}
