package org.zalando.opentracing.jdbc.span;

import io.opentracing.Span;
import lombok.AllArgsConstructor;

import java.sql.Statement;
import java.util.List;

import static org.zalando.fauxpas.FauxPas.throwingConsumer;

@AllArgsConstructor
final class CompositeSpanDecorator implements SpanDecorator {

    private final Iterable<SpanDecorator> decorators;

    @Override
    public void onQuery(
            final Span span,
            final Statement statement,
            final List<String> queries) {

        decorators.forEach(throwingConsumer(decorator ->
                decorator.onQuery(span, statement, queries)));
    }

    @Override
    public void onError(
            final Span span,
            final Statement statement,
            final Throwable error) {

        decorators.forEach(throwingConsumer(decorator ->
                decorator.onError(span, statement, error)));
    }

}
