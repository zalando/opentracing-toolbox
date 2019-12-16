package org.zalando.opentracing.servlet.extension;

import io.opentracing.Span;
import io.opentracing.log.Fields;
import org.apiguardian.api.API;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import static java.util.Collections.singletonMap;
import static org.apiguardian.api.API.Status.EXPERIMENTAL;

/**
 * Sets the <code>message</code> span log.
 *
 * @see <a href="https://opentracing.io/specification/conventions/#log-fields-table">Standard Log Fields</a>
 */
@API(status = EXPERIMENTAL)
public final class ErrorMessageSpanDecorator implements SpanDecorator {

    @Override
    public void onError(
            final HttpServletRequest request,
            final HttpServletResponse response,
            final Throwable error,
            final Span span) {

        span.log(singletonMap(Fields.MESSAGE, error.getMessage()));
    }

}
