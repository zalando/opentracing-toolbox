package org.zalando.opentracing.jdbc;

import io.opentracing.Tracer;
import lombok.AllArgsConstructor;
import net.ttddyy.dsproxy.support.ProxyDataSourceBuilder;
import org.apiguardian.api.API;
import org.zalando.opentracing.jdbc.operation.DefaultOperationName;
import org.zalando.opentracing.jdbc.operation.OperationName;
import org.zalando.opentracing.jdbc.span.SpanDecorator;
import org.zalando.opentracing.jdbc.span.StandardSpanDecorator;

import javax.annotation.CheckReturnValue;
import javax.sql.DataSource;

import static lombok.AccessLevel.PRIVATE;
import static org.apiguardian.api.API.Status.EXPERIMENTAL;
import static org.zalando.opentracing.jdbc.span.SpanDecorator.composite;

@API(status = EXPERIMENTAL)
@AllArgsConstructor(access = PRIVATE)
public final class DataSourceTracer {

    private final Tracer tracer;
    private final OperationName operationName;
    private final Lifecycle lifecycle;
    private final Activation activation;
    private final SpanDecorator decorator;

    public DataSourceTracer(final Tracer tracer) {
        this(tracer,
                new DefaultOperationName(),
                new NewSpanLifecycle(),
                new NoOpActivation(),
                new StandardSpanDecorator());
    }

    @CheckReturnValue
    public DataSourceTracer withOperationName(
            final OperationName operationName) {

        return new DataSourceTracer(
                tracer, operationName, lifecycle, activation, decorator);
    }

    @CheckReturnValue
    public DataSourceTracer withLifecycle(final Lifecycle lifecycle) {
        return new DataSourceTracer(
                tracer, operationName, lifecycle, activation, decorator);

    }

    @CheckReturnValue
    public DataSourceTracer withActivation(final Activation activation) {
        return new DataSourceTracer(
                tracer, operationName, lifecycle, activation, decorator);
    }

    /**
     * Creates a new {@link DataSourceTracer tracer} by
     * <strong>combining</strong> the {@link SpanDecorator decorator(s)} of
     * {@code this} plugin with the supplied ones.
     *
     * @param first      first decorator
     * @param decorators optional, remaining decorators
     * @return a new {@link DataSourceTracer}
     */
    @CheckReturnValue
    public DataSourceTracer withAdditionalSpanDecorators(
            final SpanDecorator first, final SpanDecorator... decorators) {
        return withSpanDecorators(decorator, composite(first, decorators));
    }

    /**
     * Creates a new {@link DataSourceTracer plugin} by
     * <strong>replacing</strong> the {@link SpanDecorator decorator(s)} of
     * {@code this} plugin with the supplied ones.
     *
     * @param decorator  first decorator
     * @param decorators optional, remaining decorators
     * @return a new {@link DataSourceTracer}
     */
    @CheckReturnValue
    public DataSourceTracer withSpanDecorators(
            final SpanDecorator decorator, final SpanDecorator... decorators) {
        return new DataSourceTracer(
                tracer, operationName, lifecycle, activation,
                composite(decorator, decorators));
    }

    @CheckReturnValue
    public DataSource trace(final DataSource dataSource) {
        return ProxyDataSourceBuilder.create(dataSource)
                .listener(new TracingQueryExecutionListener(
                        tracer, operationName, lifecycle, activation, decorator))
                .build();
    }

}
