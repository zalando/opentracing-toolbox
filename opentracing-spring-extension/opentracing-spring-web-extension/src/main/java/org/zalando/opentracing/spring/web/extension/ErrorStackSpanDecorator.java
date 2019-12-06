package org.zalando.opentracing.spring.web.extension;

import io.opentracing.Span;
import io.opentracing.log.Fields;
import org.apiguardian.api.API;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.PrintWriter;
import java.io.StringWriter;

import static java.util.Collections.singletonMap;
import static org.apiguardian.api.API.Status.EXPERIMENTAL;

@API(status = EXPERIMENTAL)
public final class ErrorStackSpanDecorator
        implements SpanDecorator {

    @Override
    public void onError(
            final HttpServletRequest request,
            final HttpServletResponse response,
            final Object handler,
            final Exception error,
            final Span span) {

        span.log(singletonMap(Fields.STACK, toString(error)));
    }

    private String toString(final Exception error) {
        final StringWriter output = new StringWriter(2048);
        error.printStackTrace(new PrintWriter(output));
        return output.toString();
    }

}
