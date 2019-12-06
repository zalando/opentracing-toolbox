package org.zalando.opentracing.spring.webflux.extension;

import io.opentracing.Span;
import io.opentracing.contrib.spring.web.webfilter.WebFluxSpanDecorator;
import lombok.AllArgsConstructor;
import org.apiguardian.api.API;
import org.springframework.web.server.ServerWebExchange;

import java.util.Arrays;

import static org.apiguardian.api.API.Status.EXPERIMENTAL;

@API(status = EXPERIMENTAL)
@AllArgsConstructor
public final class CompositeSpanDecorator implements WebFluxSpanDecorator {

    private final Iterable<WebFluxSpanDecorator> decorators;

    @Override
    public void onRequest(
            final ServerWebExchange exchange,
            final Span span) {

        decorators.forEach(decorator ->
                decorator.onRequest(exchange, span));
    }

    @Override
    public void onResponse(
            final ServerWebExchange exchange,
            final Span span) {

        decorators.forEach(decorator ->
                decorator.onResponse(exchange, span));
    }

    @Override
    public void onError(
            final ServerWebExchange exchange,
            final Throwable error,
            final Span span) {

        decorators.forEach(decorator ->
                decorator.onError(exchange, error, span));
    }

    static WebFluxSpanDecorator composite(
            final WebFluxSpanDecorator... decorators) {
        return composite(Arrays.asList(decorators));
    }

    static WebFluxSpanDecorator composite(
            final Iterable<WebFluxSpanDecorator> decorators) {
        return new CompositeSpanDecorator(decorators);
    }

}
