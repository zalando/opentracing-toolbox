package org.zalando.opentracing.spring.web.extension;

import io.opentracing.Span;
import io.opentracing.log.Fields;
import io.opentracing.tag.Tags;
import lombok.AllArgsConstructor;
import org.apiguardian.api.API;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.Map;
import java.util.function.IntPredicate;

import static org.apiguardian.api.API.Status.EXPERIMENTAL;

@API(status = EXPERIMENTAL)
@AllArgsConstructor
public final class ErrorSpanDecorator
        implements SpanDecorator {

    private final IntPredicate predicate;

    public ErrorSpanDecorator() {
        this(status -> status >= 500);
    }

    @Override
    public void onResponse(
            final HttpServletRequest request,
            final HttpServletResponse response,
            final Object handler,
            final Span span) {

        if (predicate.test(response.getStatus())) {
            span.setTag(Tags.ERROR, true);
        }

    }

    @Override
    public void onError(
            final HttpServletRequest request,
            final HttpServletResponse response,
            final Object handler,
            final Exception error,
            final Span span) {

        span.setTag(Tags.ERROR, true);

        final Map<String, Object> fields = new HashMap<>(2);
        fields.put(Fields.ERROR_KIND, error.getClass().getSimpleName());
        fields.put(Fields.ERROR_OBJECT, error);

        span.log(fields);
    }

}
