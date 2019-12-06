package org.zalando.opentracing.spring.webflux.extension;

import io.opentracing.Span;
import lombok.AllArgsConstructor;
import org.apiguardian.api.API;
import org.springframework.web.server.ServerWebExchange;

import java.util.Map;

import static org.apiguardian.api.API.Status.EXPERIMENTAL;

/**
 * Sets arbitrary, static span tags.
 */
@API(status = EXPERIMENTAL)
@AllArgsConstructor
public final class StaticSpanDecorator implements SpanDecorator {

    private final Map<String, String> tags;

    @Override
    public void onRequest(final ServerWebExchange exchange, final Span span) {
        tags.forEach(span::setTag);
    }

}
