package org.zalando.opentracing.flowid;

import io.opentracing.Span;
import lombok.AllArgsConstructor;

@AllArgsConstructor
final class CompositeFlowListener implements FlowListener {

    private final Iterable<FlowListener> listeners;

    @Override
    public void onRead(final Span span, final FlowId flowId) {
        listeners.forEach(listener ->
                listener.onRead(span, flowId));
    }

}
