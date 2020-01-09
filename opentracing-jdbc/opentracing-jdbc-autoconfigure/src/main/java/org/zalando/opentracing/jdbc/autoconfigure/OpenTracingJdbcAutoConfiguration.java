package org.zalando.opentracing.jdbc.autoconfigure;

import io.opentracing.Tracer;
import org.apiguardian.api.API;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.zalando.opentracing.jdbc.Activation;
import org.zalando.opentracing.jdbc.DataSourceTracer;
import org.zalando.opentracing.jdbc.Lifecycle;
import org.zalando.opentracing.jdbc.NewSpanLifecycle;
import org.zalando.opentracing.jdbc.NoOpActivation;
import org.zalando.opentracing.jdbc.operation.DefaultOperationName;
import org.zalando.opentracing.jdbc.operation.OperationName;
import org.zalando.opentracing.jdbc.operation.StoredProcedureOperationName;
import org.zalando.opentracing.jdbc.span.SpanDecorator;
import org.zalando.opentracing.jdbc.span.StandardSpanDecorator;

import javax.sql.DataSource;
import java.util.List;

import static org.apiguardian.api.API.Status.INTERNAL;
import static org.apiguardian.api.API.Status.STABLE;
import static org.zalando.opentracing.jdbc.span.SpanDecorator.composite;

@API(status = STABLE)
@Configuration
@ConditionalOnBean({Tracer.class, DataSource.class})
public class OpenTracingJdbcAutoConfiguration {

    @API(status = INTERNAL)
    @Configuration
    @ConditionalOnMissingBean(DataSourceTracer.class)
    public static class DataSourceTracerConfiguration {

        @Bean
        @ConditionalOnMissingBean(OperationName.class)
        public OperationName openTracingJdbcOperationName() {
            return new StoredProcedureOperationName(new DefaultOperationName());
        }

        @Bean
        @ConditionalOnMissingBean(Lifecycle.class)
        public Lifecycle openTracingJdbcLifecycle() {
            return new NewSpanLifecycle();
        }

        @Bean
        @ConditionalOnMissingBean(Activation.class)
        public Activation openTracingJdbcActivation() {
            return new NoOpActivation();
        }

        @Bean
        @ConditionalOnMissingBean(SpanDecorator.class)
        public SpanDecorator openTracingJdbcSpanDecorator() {
            return new StandardSpanDecorator();
        }

        @Bean
        public DataSourceTracer dataSourceTracer(
                final Tracer tracer,
                final OperationName operationName,
                final Lifecycle lifecycle,
                final Activation activation,
                final List<SpanDecorator> decorators) {

            return new DataSourceTracer(tracer)
                    .withOperationName(operationName)
                    .withLifecycle(lifecycle)
                    .withActivation(activation)
                    .withSpanDecorators(composite(decorators));
        }

    }

    @API(status = INTERNAL)
    @Bean
    @ConditionalOnProperty(name = "opentracing.jdbc.enabled", havingValue = "true", matchIfMissing = true)
    public BeanPostProcessor tracingDataSourceBeanPostProcessor(
            final DataSourceTracer tracer) {
        return new TracingProcessor(tracer);
    }

}
