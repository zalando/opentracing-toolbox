package org.zalando.opentracing.spring.webflux.extension;

import io.opentracing.Span;
import io.opentracing.contrib.spring.web.webfilter.WebFluxSpanDecorator;
import org.springframework.web.server.ServerWebExchange;

interface SpanDecorator extends WebFluxSpanDecorator {

    @Override
    default void onRequest(
            final ServerWebExchange exchange,
            final Span span) {

        // nothing to do
    }

    @Override
    default void onResponse(
            final ServerWebExchange exchange,
            final Span span) {

        // nothing to do
    }

    @Override
    default void onError(
            final ServerWebExchange exchange,
            final Throwable error,
            final Span span) {

        // nothing to do
    }

}
