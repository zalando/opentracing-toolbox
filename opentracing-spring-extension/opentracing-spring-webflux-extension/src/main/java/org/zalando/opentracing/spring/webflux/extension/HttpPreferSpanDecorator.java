package org.zalando.opentracing.spring.webflux.extension;

import io.opentracing.Span;
import io.opentracing.tag.StringTag;
import io.opentracing.tag.Tag;
import org.apiguardian.api.API;
import org.springframework.http.HttpHeaders;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.web.server.ServerWebExchange;

import java.util.Optional;

import static org.apiguardian.api.API.Status.EXPERIMENTAL;

@API(status = EXPERIMENTAL)
public final class HttpPreferSpanDecorator implements SpanDecorator {

    private static final Tag<String> PREFER = new StringTag("http.prefer");

    @Override
    public void onRequest(final ServerWebExchange exchange, final Span span) {
        final ServerHttpRequest request = exchange.getRequest();
        final HttpHeaders headers = request.getHeaders();

        Optional.ofNullable(headers.getFirst("Prefer"))
                .ifPresent(prefer -> span.setTag(PREFER, prefer));
    }

}
