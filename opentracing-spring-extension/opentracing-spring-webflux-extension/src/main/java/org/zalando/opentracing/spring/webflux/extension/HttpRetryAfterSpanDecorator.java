package org.zalando.opentracing.spring.webflux.extension;

import io.opentracing.Span;
import io.opentracing.tag.StringTag;
import io.opentracing.tag.Tag;
import org.apiguardian.api.API;
import org.springframework.http.HttpHeaders;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.web.server.ServerWebExchange;

import java.util.Optional;

import static org.apiguardian.api.API.Status.EXPERIMENTAL;

@API(status = EXPERIMENTAL)
public final class HttpRetryAfterSpanDecorator implements SpanDecorator {

    private static final Tag<String> RETRY_AFTER = new StringTag("http.retry_after");

    @Override
    public void onResponse(final ServerWebExchange exchange, final Span span) {
        final ServerHttpResponse response = exchange.getResponse();
        final HttpHeaders headers = response.getHeaders();

        Optional.ofNullable(headers.getFirst("Retry-After"))
                .ifPresent(prefer -> span.setTag(RETRY_AFTER, prefer));
    }

}
