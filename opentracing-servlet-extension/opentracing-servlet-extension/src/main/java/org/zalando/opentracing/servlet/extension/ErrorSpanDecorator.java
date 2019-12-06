package org.zalando.opentracing.servlet.extension;

import io.opentracing.Span;
import io.opentracing.log.Fields;
import io.opentracing.tag.Tags;
import lombok.AllArgsConstructor;
import org.apiguardian.api.API;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Predicate;

import static org.apiguardian.api.API.Status.EXPERIMENTAL;
import static org.zalando.fauxpas.FauxPas.throwingPredicate;

/**
 * Sets the <code>error</code> span tag as well as the <code>error.kind</code> and <code>error.object</code> span logs.
 *
 * @see <a href="https://opentracing.io/specification/conventions/#span-tags-table">Standard Span Tags</a>
 * @see <a href="https://opentracing.io/specification/conventions/#log-fields-table">Standard Log Fields</a>
 */
@API(status = EXPERIMENTAL)
@AllArgsConstructor
public final class ErrorSpanDecorator implements SpanDecorator {

    private final Predicate<HttpServletResponse> predicate;

    public ErrorSpanDecorator() {
        this(throwingPredicate(response ->
                response.getStatus() >= 500));
    }

    @Override
    public void onResponse(
            final HttpServletRequest request,
            final HttpServletResponse response,
            final Span span) {

        if (predicate.test(response)) {
            span.setTag(Tags.ERROR, true);
        }
    }

    @Override
    public void onError(
            final HttpServletRequest request,
            final HttpServletResponse response,
            final Throwable error,
            final Span span) {

        span.setTag(Tags.ERROR, true);

        final Map<String, Object> fields = new HashMap<>();
        fields.put(Fields.ERROR_KIND, error.getClass().getSimpleName());
        fields.put(Fields.ERROR_OBJECT, error);

        span.log(fields);
    }

}
