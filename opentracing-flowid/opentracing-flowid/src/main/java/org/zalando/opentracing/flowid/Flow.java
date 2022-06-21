package org.zalando.opentracing.flowid;

import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.UnaryOperator;

import javax.annotation.Nullable;

import io.opentracing.Tracer;
import static org.zalando.opentracing.flowid.FlowListener.*;

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

    /**
     * deprecated - please use {@link #currentSpanId()} because it doesn't throw an exception
     */
    @Deprecated
    String currentId() throws IllegalStateException;

    Optional<String> currentSpanId();

    void writeTo(BiConsumer<String, String> writer);

    @Nullable <T> T write(BiFunction<String, String, T> writer);

    static Flow create(final Tracer tracer) {
        return create(tracer, new TagFlowListener());
    }

    static Flow create(final Tracer tracer, final FlowListener... listeners) {
        return new DefaultFlow(tracer, composite(listeners));
    }

}
