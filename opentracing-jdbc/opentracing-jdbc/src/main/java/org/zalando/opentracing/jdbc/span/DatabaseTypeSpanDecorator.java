package org.zalando.opentracing.jdbc.span;

import io.opentracing.Span;
import lombok.AllArgsConstructor;
import org.apiguardian.api.API;

import java.sql.Statement;
import java.util.List;

import static io.opentracing.tag.Tags.DB_TYPE;
import static org.apiguardian.api.API.Status.EXPERIMENTAL;

/**
 * Sets the <code>db.type</code> span tag, defaults to <code>sql</code>.
 *
 * @see <a href="https://opentracing.io/specification/conventions/#span-tags-table">Standard Span Tags</a>
 */
@API(status = EXPERIMENTAL)
@AllArgsConstructor
public final class DatabaseTypeSpanDecorator implements SpanDecorator {

    private final String type;

    public DatabaseTypeSpanDecorator() {
        this("sql");
    }

    @Override
    public void onQuery(
            final Span span,
            final Statement statement,
            final List<String> queries) {

        span.setTag(DB_TYPE, type);
    }

}
