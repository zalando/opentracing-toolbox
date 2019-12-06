package org.zalando.opentracing.spring.webflux.extension;

import io.opentracing.Span;
import io.opentracing.tag.StringTag;
import io.opentracing.tag.Tag;
import org.apiguardian.api.API;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.server.ServerWebExchange;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Stream;

import static org.apiguardian.api.API.Status.EXPERIMENTAL;
import static org.springframework.web.reactive.HandlerMapping.BEST_MATCHING_HANDLER_ATTRIBUTE;
import static org.springframework.web.reactive.HandlerMapping.BEST_MATCHING_PATTERN_ATTRIBUTE;

@API(status = EXPERIMENTAL)
public final class HttpPathSpanDecorator
        implements SpanDecorator {

    private static final Tag<String> HTTP_PATH = new StringTag("http.path");

    @Override
    public void onResponse(
            final ServerWebExchange exchange,
            final Span span) {

        tag(exchange, span);
    }

    @Override
    public void onError(
            final ServerWebExchange exchange,
            final Throwable error,
            final Span span) {

        tag(exchange, span);
    }

    private void tag(final ServerWebExchange exchange, final Span span) {
        final Stream<Function<ServerWebExchange, Optional<String>>> stream =
                Stream.of(this::getPath, this::getPattern);

        stream.map(function -> function.apply(exchange))
                .filter(Optional::isPresent)
                .map(Optional::get)
                .findFirst()
                .ifPresent(path -> span.setTag(HTTP_PATH, path));
    }

    private Optional<String> getPath(final ServerWebExchange exchange) {
        return Optional.ofNullable(exchange.getAttribute(BEST_MATCHING_HANDLER_ATTRIBUTE))
                .filter(HandlerMethod.class::isInstance)
                .map(HandlerMethod.class::cast)
                // TODO verify that we see class-level @RequestMapping paths
                .map(method -> method.getMethodAnnotation(RequestMapping.class))
                .map(RequestMapping::path)
                .map(Arrays::asList)
                .map(List::iterator)
                .filter(Iterator::hasNext)
                .map(Iterator::next);
    }


    private Optional<String> getPattern(final ServerWebExchange exchange) {
        return Optional.ofNullable(exchange.getAttribute(BEST_MATCHING_PATTERN_ATTRIBUTE))
                .map(Object::toString);
    }

}
