package org.zalando.opentracing.spring.webflux.extension;

import io.opentracing.Span;
import io.opentracing.tag.Tags;
import org.apiguardian.api.API;
import org.springframework.web.server.ServerWebExchange;

import static org.apiguardian.api.API.Status.EXPERIMENTAL;

@API(status = EXPERIMENTAL)
public final class HttpMethodSpanDecorator implements SpanDecorator {

    @Override
    public void onRequest(final ServerWebExchange exchange, final Span span) {
        span.setTag(Tags.HTTP_METHOD, exchange.getRequest().getMethodValue());
    }

}
