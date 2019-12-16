package org.zalando.opentracing.spring.webflux.extension;

import io.opentracing.Span;
import io.opentracing.log.Fields;
import org.apiguardian.api.API;
import org.springframework.web.server.ServerWebExchange;

import java.io.PrintWriter;
import java.io.StringWriter;

import static java.util.Collections.singletonMap;
import static org.apiguardian.api.API.Status.EXPERIMENTAL;

@API(status = EXPERIMENTAL)
public final class ErrorStackSpanDecorator
        implements SpanDecorator {

    @Override
    public void onError(
            final ServerWebExchange exchange,
            final Throwable error,
            final Span span) {

        span.log(singletonMap(Fields.STACK, toString(error)));
    }

    private String toString(final Throwable error) {
        final StringWriter output = new StringWriter(2048);
        error.printStackTrace(new PrintWriter(output));
        return output.toString();
    }

}
