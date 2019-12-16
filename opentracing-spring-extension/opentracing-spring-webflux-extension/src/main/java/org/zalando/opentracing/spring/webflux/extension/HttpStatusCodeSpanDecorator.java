package org.zalando.opentracing.spring.webflux.extension;

import io.opentracing.Span;
import org.apiguardian.api.API;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.web.server.ServerWebExchange;

import java.util.Optional;

import static io.opentracing.tag.Tags.HTTP_STATUS;
import static org.apiguardian.api.API.Status.EXPERIMENTAL;

/**
 * Sets the <code>http.status_code</code> span tag.
 *
 * @see <a href="https://opentracing.io/specification/conventions/#span-tags-table">Standard Span Tags</a>
 */
@API(status = EXPERIMENTAL)
public final class HttpStatusCodeSpanDecorator implements SpanDecorator {

    @Override
    public void onResponse(
            final ServerWebExchange exchange,
            final Span span) {

        final ServerHttpResponse response = exchange.getResponse();
        Optional.ofNullable(response.getStatusCode()).ifPresent(status ->
                span.setTag(HTTP_STATUS, status.value()));
    }

}
