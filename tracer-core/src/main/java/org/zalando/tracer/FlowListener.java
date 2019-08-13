package org.zalando.tracer;

import io.opentracing.Span;
import org.apiguardian.api.API;

import java.util.Arrays;

import static org.apiguardian.api.API.Status.EXPERIMENTAL;

@API(status = EXPERIMENTAL)
public interface FlowListener {

    void onRead(final Span span, final FlowId flowId);

    static FlowListener composite(final FlowListener... listeners) {
        return composite(Arrays.asList(listeners));
    }

    static FlowListener composite(final Iterable<FlowListener> listeners) {
        return new CompositeFlowListener(listeners);
    }

}
