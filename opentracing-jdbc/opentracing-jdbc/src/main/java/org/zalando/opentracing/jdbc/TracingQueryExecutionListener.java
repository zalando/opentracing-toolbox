package org.zalando.opentracing.jdbc;

import io.opentracing.Scope;
import io.opentracing.Span;
import io.opentracing.Tracer;
import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import net.ttddyy.dsproxy.ExecutionInfo;
import net.ttddyy.dsproxy.QueryInfo;
import net.ttddyy.dsproxy.listener.QueryExecutionListener;
import org.zalando.opentracing.jdbc.operation.OperationName;
import org.zalando.opentracing.jdbc.span.SpanDecorator;

import java.lang.reflect.Method;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import java.util.Optional;

import static java.util.stream.Collectors.toList;

@AllArgsConstructor
final class TracingQueryExecutionListener implements QueryExecutionListener {

    private final Tracer tracer;
    private final OperationName operationName;
    private final Lifecycle lifecycle;
    private final Activation activation;
    private final SpanDecorator decorator;

    @Override
    public void beforeQuery(
            final ExecutionInfo info, final List<QueryInfo> infos) {

        final Method method = info.getMethod();
        final List<String> queries = infos.stream()
                .map(QueryInfo::getQuery)
                .collect(toList());

        final String name = operationName.generate(method, queries);

        lifecycle.start(tracer, name).ifPresent(span ->
                beforeQuery(info, queries, span));
    }

    @SneakyThrows(SQLException.class)
    private void beforeQuery(
            final ExecutionInfo info,
            final List<String> queries,
            final Span span) {

        final Scope scope = activation.activate(tracer, span);

        final Statement statement = info.getStatement();
        decorator.onQuery(span, statement, queries);

        save(info, Span.class, span);
        save(info, Scope.class, scope);
    }

    @Override
    public void afterQuery(
            final ExecutionInfo info, final List<QueryInfo> queries) {

        restore(info, Span.class).ifPresent(span ->
                afterQuery(info, span));
    }

    @SneakyThrows(SQLException.class)
    private void afterQuery(final ExecutionInfo info, final Span span) {
        try {
            if (!info.isSuccess()) {
                final Statement statement = info.getStatement();
                final Throwable error = info.getThrowable();

                decorator.onError(span, statement, error);
            }
        } finally {
            try {
                restore(info, Scope.class).ifPresent(Scope::close);
            } finally {
                span.finish();
            }
        }
    }

    private <T> void save(
            final ExecutionInfo info, final Class<T> type, final T value) {

        info.addCustomValue(type.getName(), value);
    }

    private <T> Optional<T> restore(
            final ExecutionInfo info, final Class<T> type) {

        return Optional.ofNullable(info.getCustomValue(type.getName(), type));
    }

}
