package org.zalando.opentracing.jdbc.span;

import io.opentracing.Span;
import org.apiguardian.api.API;

import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;

import static io.opentracing.log.Fields.ERROR_KIND;
import static io.opentracing.log.Fields.ERROR_OBJECT;
import static io.opentracing.tag.Tags.ERROR;
import static org.apiguardian.api.API.Status.EXPERIMENTAL;

/**
 * Sets the <code>error</code> span tag as well as the <code>error.kind</code> and <code>error.object</code> span logs.
 *
 * @see <a href="https://opentracing.io/specification/conventions/#span-tags-table">Standard Span Tags</a>
 * @see <a href="https://opentracing.io/specification/conventions/#log-fields-table">Standard Log Fields</a>
 */
@API(status = EXPERIMENTAL)
public final class ErrorSpanDecorator implements SpanDecorator {

    @Override
    public void onError(
            final Span span,
            final Statement statement,
            final Throwable error) {

        span.setTag(ERROR, true);

        final Map<String, Object> tags = new HashMap<>(2, 1.0f);
        tags.put(ERROR_KIND, error.getClass().getSimpleName());
        tags.put(ERROR_OBJECT, error);

        span.log(tags);
    }

}
