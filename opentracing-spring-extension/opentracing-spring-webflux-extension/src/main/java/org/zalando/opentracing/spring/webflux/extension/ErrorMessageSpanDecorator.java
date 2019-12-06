package org.zalando.opentracing.spring.webflux.extension;

import io.opentracing.Span;
import io.opentracing.log.Fields;
import org.apiguardian.api.API;
import org.springframework.web.server.ServerWebExchange;

import static java.util.Collections.singletonMap;
import static org.apiguardian.api.API.Status.EXPERIMENTAL;

@API(status = EXPERIMENTAL)
public final class ErrorMessageSpanDecorator implements SpanDecorator {

    @Override
    public void onError(
            final ServerWebExchange exchange,
            final Throwable error,
            final Span span) {

        span.log(singletonMap(Fields.MESSAGE, error.getMessage()));
    }

}
