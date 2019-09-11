package org.zalando.tracer;

import io.opentracing.Tracer;

import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.UnaryOperator;

import static org.zalando.tracer.FlowListener.composite;

public interface Flow {

    interface Header {
        String FLOW_ID = "X-Flow-ID";
    }

    interface Tag {
        String FLOW_ID = "flow_id";
    }

    interface Baggage {
        String FLOW_ID = "flow_id";
    }

    interface Logging {
        String TRACE_ID = "trace_id";
        String SPAN_ID = "span_id";
        String FLOW_ID = "flow_id";
    }

    void readFrom(UnaryOperator<String> reader);

    String currentId() throws IllegalStateException;

    void writeTo(BiConsumer<String, String> writer);

    <T> T write(BiFunction<String, String, T> writer);

    static Flow create(final Tracer tracer) {
        return create(tracer, new TagFlowListener());
    }

    static Flow create(final Tracer tracer, final FlowListener... listeners) {
        return new DefaultFlow(tracer, composite(listeners));
    }

}
