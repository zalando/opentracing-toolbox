package org.zalando.opentracing.jdbc.span;

import io.opentracing.Span;
import org.apiguardian.api.API;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

import static io.opentracing.tag.Tags.DB_INSTANCE;
import static org.apiguardian.api.API.Status.EXPERIMENTAL;

/**
 * Sets the <code>db.instance</code> span tag.
 *
 * @see <a href="https://opentracing.io/specification/conventions/#span-tags-table">Standard Span Tags</a>
 */
@API(status = EXPERIMENTAL)
public final class DatabaseInstanceSpanDecorator implements SpanDecorator {

    @Override
    public void onQuery(
            final Span span,
            final Statement statement,
            final List<String> queries) throws SQLException {

        final Connection connection = statement.getConnection();
        span.setTag(DB_INSTANCE, connection.getCatalog());
    }

}
