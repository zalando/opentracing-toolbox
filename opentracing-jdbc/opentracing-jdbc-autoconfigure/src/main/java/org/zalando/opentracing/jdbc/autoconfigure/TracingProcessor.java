package org.zalando.opentracing.jdbc.autoconfigure;

import org.zalando.opentracing.jdbc.DataSourceTracer;

import javax.sql.DataSource;

final class TracingProcessor extends ForwardingProcessor {

    public TracingProcessor(final DataSourceTracer tracer) {
        super(new ConditionalProxyProcessor<>(DataSource.class, tracer::trace));
    }

}
