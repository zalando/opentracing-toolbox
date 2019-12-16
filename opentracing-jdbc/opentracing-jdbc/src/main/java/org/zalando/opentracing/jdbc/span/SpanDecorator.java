package org.zalando.opentracing.jdbc.span;

import io.opentracing.Span;
import org.apiguardian.api.API;

import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static java.util.Collections.unmodifiableList;
import static org.apiguardian.api.API.Status.EXPERIMENTAL;

@API(status = EXPERIMENTAL)
public interface SpanDecorator {

    default void onQuery(final Span span, final Statement statement, final List<String> queries) throws SQLException {
        // nothing to do
    }

    // TODO onResult?

    default void onError(final Span span, final Statement statement, final Throwable error) throws SQLException {
        // nothing to do
    }

    static SpanDecorator composite(final SpanDecorator decorator, final SpanDecorator... decorators) {
        final List<SpanDecorator> list = new ArrayList<>(decorators.length + 1);

        list.add(decorator);
        Collections.addAll(list, decorators);

        return composite(unmodifiableList(list));
    }

    static SpanDecorator composite(final Iterable<SpanDecorator> decorators) {
        return new CompositeSpanDecorator(decorators);
    }

}
