package org.zalando.opentracing.jdbc.autoconfigure;

import org.springframework.beans.factory.BeanFactory;
import org.zalando.opentracing.jdbc.DataSourceTracer;

import javax.sql.DataSource;

final class TracingProcessor extends ForwardingProcessor {

    public TracingProcessor(final BeanFactory beanFactory) {
        super(new ConditionalProxyProcessor<>(DataSource.class, dataSource -> wrap(beanFactory, dataSource)));
    }

    private static DataSource wrap(final BeanFactory beanFactory, final DataSource dataSource) {
        final DataSourceTracer tracer = beanFactory.getBean(DataSourceTracer.class);
        return tracer.trace(dataSource);
    }

}
