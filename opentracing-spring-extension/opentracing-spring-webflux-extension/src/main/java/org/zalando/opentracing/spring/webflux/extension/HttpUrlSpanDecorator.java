package org.zalando.opentracing.spring.webflux.extension;

import io.opentracing.Span;
import io.opentracing.tag.Tags;
import org.apiguardian.api.API;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.web.server.ServerWebExchange;

import static org.apiguardian.api.API.Status.EXPERIMENTAL;

@API(status = EXPERIMENTAL)
public final class HttpUrlSpanDecorator implements SpanDecorator {

    @Override
    public void onRequest(final ServerWebExchange exchange, final Span span) {
        final ServerHttpRequest request = exchange.getRequest();
        final String url = request.getURI().toString();
        span.setTag(Tags.HTTP_URL, url);
    }

}
