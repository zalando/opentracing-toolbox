package org.zalando.opentracing.spring.webflux.extension;

import io.opentracing.Span;
import io.opentracing.tag.StringTag;
import io.opentracing.tag.Tag;
import org.apiguardian.api.API;
import org.springframework.http.HttpHeaders;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.web.server.ServerWebExchange;

import java.util.Objects;
import java.util.stream.Stream;

import static org.apiguardian.api.API.Status.EXPERIMENTAL;

@API(status = EXPERIMENTAL)
public final class HttpMethodOverrideSpanDecorator implements SpanDecorator {

    private static final Tag<String> HTTP_METHOD_OVERRIDE =
            new StringTag("http.method_override");

    @Override
    public void onRequest(final ServerWebExchange exchange, final Span span) {
        final ServerHttpRequest request = exchange.getRequest();
        final HttpHeaders headers = request.getHeaders();

        Stream.of("HTTP-Method-Override", "X-HTTP-Method-Override")
                .map(headers::getFirst)
                .filter(Objects::nonNull)
                .findFirst()
                .ifPresent(override ->
                        span.setTag(HTTP_METHOD_OVERRIDE, override));
    }

}
