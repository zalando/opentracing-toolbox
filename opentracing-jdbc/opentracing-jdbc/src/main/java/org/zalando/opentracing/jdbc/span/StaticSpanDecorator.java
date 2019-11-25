package org.zalando.opentracing.jdbc.span;

import io.opentracing.Span;
import org.apiguardian.api.API;

import java.sql.Statement;
import java.util.List;
import java.util.Map;

import static org.apiguardian.api.API.Status.EXPERIMENTAL;

/**
 * Sets arbitrary, static span tags.
 */
@API(status = EXPERIMENTAL)
public final class StaticSpanDecorator implements SpanDecorator {

    private final Map<String, String> tags;

    public StaticSpanDecorator(final Map<String, String> tags) {
        this.tags = tags;
    }

    @Override
    public void onQuery(final Span span, final Statement statement, final List<String> queries) {
        tags.forEach(span::setTag);
    }

}
