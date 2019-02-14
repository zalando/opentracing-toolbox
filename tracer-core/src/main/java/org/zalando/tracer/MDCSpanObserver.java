package org.zalando.tracer;

import io.opentracing.contrib.api.SpanData;
import io.opentracing.contrib.api.SpanObserver;
import io.opentracing.contrib.api.TracerObserver;
import org.slf4j.MDC;

import java.util.Map;

import static org.zalando.tracer.Flow.Logging.FLOW_ID;
import static org.zalando.tracer.Flow.Logging.SPAN_ID;
import static org.zalando.tracer.Flow.Logging.TRACE_ID;

/**
 * %equals{%X{flow_id}}{}{%X{Trace-ID}}
 */
public final class MDCSpanObserver implements TracerObserver, SpanObserver {

    @Override
    public SpanObserver onStart(final SpanData data) {
        // TODO https://github.com/opentracing-contrib/java-api-extensions/issues/17
        MDC.put(TRACE_ID, data.getCorrelationId().toString());
        MDC.put(SPAN_ID, data.getCorrelationId().toString());
        return this;
    }

    @Override
    public void onSetOperationName(final SpanData data, final String operationName) {

    }

    @Override
    public void onSetTag(final SpanData data, final String key, final Object value) {

    }

    @Override
    public void onSetBaggageItem(final SpanData data, final String key, final String value) {
        if (Flow.Baggage.FLOW_ID.equals(key)) {
            MDC.put(FLOW_ID, value);
        }
    }

    @Override
    public void onLog(final SpanData data, final long timestamp, final Map<String, ?> fields) {

    }

    @Override
    public void onLog(final SpanData data, final long timestamp, final String fields) {

    }

    @Override
    public void onFinish(final SpanData data, final long timestamp) {
        MDC.remove(FLOW_ID);
        MDC.remove(SPAN_ID);
        MDC.remove(TRACE_ID);
    }

}