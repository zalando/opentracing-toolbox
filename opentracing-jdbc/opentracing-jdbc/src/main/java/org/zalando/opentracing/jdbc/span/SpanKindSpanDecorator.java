package org.zalando.opentracing.jdbc.span;

import io.opentracing.Span;
import org.apiguardian.api.API;

import java.sql.Statement;
import java.util.List;

import static io.opentracing.tag.Tags.SPAN_KIND;
import static io.opentracing.tag.Tags.SPAN_KIND_CLIENT;
import static org.apiguardian.api.API.Status.EXPERIMENTAL;

/**
 * Sets the <code>span.kind</code> span tag.
 *
 * @see <a href="https://opentracing.io/specification/conventions/#span-tags-table">Standard Span Tags</a>
 */
@API(status = EXPERIMENTAL)
public final class SpanKindSpanDecorator implements SpanDecorator {

    private final String kind;

    public SpanKindSpanDecorator() {
        this(SPAN_KIND_CLIENT);
    }

    public SpanKindSpanDecorator(final String kind) {
        this.kind = kind;
    }

    @Override
    public void onQuery(final Span span, final Statement statement, final List<String> queries) {
        span.setTag(SPAN_KIND, kind);
    }

}
