package org.zalando.opentracing.spring.webflux.extension;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.Predicate;

import io.opentracing.Span;
import io.opentracing.log.Fields;
import io.opentracing.tag.Tags;
import lombok.AllArgsConstructor;
import org.apiguardian.api.API;
import static org.apiguardian.api.API.Status.*;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ServerWebExchange;

@API(status = EXPERIMENTAL)
@AllArgsConstructor
public final class ErrorSpanDecorator
        implements SpanDecorator {

    private final Predicate<HttpStatus> predicate;

    public ErrorSpanDecorator() {
        this(HttpStatus::is5xxServerError);
    }

    @Override
    public void onResponse(
            final ServerWebExchange exchange,
            final Span span) {

        HttpStatus statusCode = exchange.getResponse().getStatusCode();
        if (statusCode == null || predicate.test(statusCode)) {
            span.setTag(Tags.ERROR, true);
        }
    }

    @Override
    public void onError(
            final ServerWebExchange exchange,
            final Throwable error,
            final Span span) {

        span.setTag(Tags.ERROR, true);

        final Map<String, Object> fields = new HashMap<>(2);
        fields.put(Fields.ERROR_KIND, error.getClass().getSimpleName());
        fields.put(Fields.ERROR_OBJECT, error);

        span.log(fields);
    }

}
