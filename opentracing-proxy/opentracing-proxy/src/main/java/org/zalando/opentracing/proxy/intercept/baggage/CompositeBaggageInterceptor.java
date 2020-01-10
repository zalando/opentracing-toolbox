package org.zalando.opentracing.proxy.intercept.baggage;

import io.opentracing.Span;
import lombok.AllArgsConstructor;

import java.util.Collection;
import java.util.Map;
import java.util.Map.Entry;

import static java.util.stream.Collectors.toMap;

@AllArgsConstructor
final class CompositeBaggageInterceptor implements BaggageInterceptor {

    private final Collection<BaggageInterceptor> interceptors;

    @Override
    public Map<String, String> intercept(
            final Span span, final String key, final String value) {

        return interceptors.stream()
                .flatMap(interceptor -> interceptor
                        .intercept(span, key, value).entrySet().stream())
                .collect(toMap(Entry::getKey, Entry::getValue,
                        this::firstComeFirstServe));
    }

    @SuppressWarnings("PMD.UnusedFormalParameter")
    private <T> T firstComeFirstServe(final T first, final T ignored) {
        return first;
    }

}
