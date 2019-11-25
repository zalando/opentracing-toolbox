package org.zalando.opentracing.jdbc.span;

import io.opentracing.Span;
import lombok.AllArgsConstructor;
import org.apiguardian.api.API;

import java.sql.Statement;
import java.util.List;

import static io.opentracing.tag.Tags.COMPONENT;
import static org.apiguardian.api.API.Status.EXPERIMENTAL;

/**
 * Sets the <code>component</code> span tag, defaults to <code>JDBC</code>.
 *
 * @see <a href="https://opentracing.io/specification/conventions/#span-tags-table">Standard Span Tags</a>
 */
@API(status = EXPERIMENTAL)
@AllArgsConstructor
public final class ComponentSpanDecorator implements SpanDecorator {

    private final String component;

    public ComponentSpanDecorator() {
        this("JDBC");
    }

    @Override
    public void onQuery(
            final Span span,
            final Statement statement,
            final List<String> queries) {

        span.setTag(COMPONENT, component);
    }

}
