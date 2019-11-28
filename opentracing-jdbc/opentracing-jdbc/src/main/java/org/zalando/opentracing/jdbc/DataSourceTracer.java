package org.zalando.opentracing.jdbc;

import io.opentracing.Tracer;
import lombok.AllArgsConstructor;
import net.ttddyy.dsproxy.support.ProxyDataSourceBuilder;
import org.apiguardian.api.API;
import org.zalando.opentracing.jdbc.operation.DefaultOperationName;
import org.zalando.opentracing.jdbc.operation.OperationName;
import org.zalando.opentracing.jdbc.span.ComponentSpanDecorator;
import org.zalando.opentracing.jdbc.span.DatabaseInstanceSpanDecorator;
import org.zalando.opentracing.jdbc.span.DatabaseStatementSpanDecorator;
import org.zalando.opentracing.jdbc.span.DatabaseTypeSpanDecorator;
import org.zalando.opentracing.jdbc.span.DatabaseUserSpanDecorator;
import org.zalando.opentracing.jdbc.span.ErrorMessageSpanDecorator;
import org.zalando.opentracing.jdbc.span.ErrorSpanDecorator;
import org.zalando.opentracing.jdbc.span.ErrorStackSpanDecorator;
import org.zalando.opentracing.jdbc.span.PeerSpanDecorator;
import org.zalando.opentracing.jdbc.span.SpanDecorator;
import org.zalando.opentracing.jdbc.span.SpanKindSpanDecorator;

import javax.annotation.CheckReturnValue;
import javax.sql.DataSource;

import static java.util.ServiceLoader.load;
import static lombok.AccessLevel.PRIVATE;
import static org.apiguardian.api.API.Status.EXPERIMENTAL;
import static org.zalando.opentracing.jdbc.span.SpanDecorator.composite;

@API(status = EXPERIMENTAL)
@AllArgsConstructor(access = PRIVATE)
public final class DataSourceTracer {

    private final Tracer tracer;
    private final OperationName operationName;
    private final SpanDecorator decorator;

    public DataSourceTracer(final Tracer tracer) {
        this(tracer, new DefaultOperationName(), composite(
                new ComponentSpanDecorator(),
                new DatabaseInstanceSpanDecorator(),
                new DatabaseStatementSpanDecorator(),
                new DatabaseTypeSpanDecorator(),
                new DatabaseUserSpanDecorator(),
                new ErrorMessageSpanDecorator(),
                new ErrorSpanDecorator(),
                new ErrorStackSpanDecorator(),
                new PeerSpanDecorator(),
                new SpanKindSpanDecorator(),
                composite(load(SpanDecorator.class))
        ));
    }

    @CheckReturnValue
    public DataSourceTracer withOperationName(final OperationName operationName) {
        return new DataSourceTracer(tracer, operationName, decorator);
    }

    /**
     * Creates a new {@link DataSourceTracer tracer} by <strong>combining</strong> the {@link SpanDecorator decorator(s)} of
     * {@code this} plugin with the supplied ones.
     *
     * @param first      first decorator
     * @param decorators optional, remaining decorators
     * @return a new {@link DataSourceTracer}
     */
    @CheckReturnValue
    public DataSourceTracer withAdditionalSpanDecorators(final SpanDecorator first,
            final SpanDecorator... decorators) {
        return withSpanDecorators(decorator, composite(first, decorators));
    }

    /**
     * Creates a new {@link DataSourceTracer plugin} by <strong>replacing</strong> the {@link SpanDecorator decorator(s)} of
     * {@code this} plugin with the supplied ones.
     *
     * @param decorator  first decorator
     * @param decorators optional, remaining decorators
     * @return a new {@link DataSourceTracer}
     */
    @CheckReturnValue
    public DataSourceTracer withSpanDecorators(final SpanDecorator decorator, final SpanDecorator... decorators) {
        return new DataSourceTracer(tracer, operationName, composite(decorator, decorators));
    }

    @CheckReturnValue
    public DataSource trace(final DataSource dataSource) {
        return ProxyDataSourceBuilder.create(dataSource)
                .listener(new TracingQueryExecutionListener(tracer, operationName, decorator))
                .build();
    }

}
