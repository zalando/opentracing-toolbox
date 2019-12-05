package org.zalando.opentracing.jdbc;

import io.opentracing.Span;
import io.opentracing.Tracer;
import io.opentracing.tag.Tags;
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

import static java.util.stream.Collectors.toList;

@AllArgsConstructor
final class TracingQueryExecutionListener implements QueryExecutionListener {

    private static final String SPAN = TracingQueryExecutionListener.class.getName() + ".SPAN";

    private final Tracer tracer;
    private final OperationName operationName;
    private final SpanDecorator decorator;

    @Override
    @SneakyThrows(SQLException.class)
    public void beforeQuery(final ExecutionInfo info, final List<QueryInfo> infos) {
        final Method method = info.getMethod();
        final List<String> queries = infos.stream()
                .map(QueryInfo::getQuery)
                .collect(toList());

        final String name = operationName.generate(method, queries);
        final Span span = tracer.buildSpan(name)
                .withTag(Tags.SPAN_KIND, Tags.SPAN_KIND_CLIENT)
                .start();
        final Statement statement = info.getStatement();

        decorator.onQuery(span, statement, queries);

        info.addCustomValue(SPAN, span);
    }

    @Override
    @SneakyThrows(SQLException.class)
    public void afterQuery(final ExecutionInfo info, final List<QueryInfo> queries) {
        final Span span = info.getCustomValue(SPAN, Span.class);

        try {
            if (!info.isSuccess()) {
                final Statement statement = info.getStatement();
                final Throwable error = info.getThrowable();

                decorator.onError(span, statement, error);
            }
        } finally {
            span.finish();
        }
    }

}
