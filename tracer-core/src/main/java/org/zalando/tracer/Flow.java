package org.zalando.tracer;

import io.opentracing.Tracer;

import java.util.function.BiConsumer;
import java.util.function.Function;

public interface Flow {

    interface Header {
        String FLOW_ID = "X-Flow-ID";
    }

    interface Baggage {
        String FLOW_ID = "flow_id";
    }

    interface Logging {
        String TRACE_ID = "trace_id";
        String SPAN_ID = "span_id";
        String FLOW_ID = "flow_id";
    }

    void readFrom(Function<String, String> reader);

    /**
     *
     * @return
     * @throws IllegalStateException
     */
    String currentId();

    void writeTo(BiConsumer<String, String> writer);

    static Flow create(final Tracer tracer) {
        return new DefaultFlow(tracer);
    }


}
