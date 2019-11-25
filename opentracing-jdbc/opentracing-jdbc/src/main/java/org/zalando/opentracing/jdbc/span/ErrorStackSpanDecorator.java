package org.zalando.opentracing.jdbc.span;

import io.opentracing.Span;
import org.apiguardian.api.API;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.sql.Statement;

import static io.opentracing.log.Fields.STACK;
import static java.util.Collections.singletonMap;
import static org.apiguardian.api.API.Status.EXPERIMENTAL;

/**
 * Sets the <code>stack</code> span log.
 *
 * @see <a href="https://opentracing.io/specification/conventions/#log-fields-table">Standard Log Fields</a>
 */
@API(status = EXPERIMENTAL)
public final class ErrorStackSpanDecorator implements SpanDecorator {

    @Override
    public void onError(
            final Span span,
            final Statement statement,
            final Throwable error) {

        span.log(singletonMap(STACK, stackTraceAsString(error)));
    }

    private String stackTraceAsString(final Throwable throwable) {
        final StringWriter output = new StringWriter();
        throwable.printStackTrace(new PrintWriter(output));
        return output.toString();
    }

}
