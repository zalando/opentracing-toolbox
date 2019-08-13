package org.zalando.tracer;

import io.opentracing.Span;
import org.apiguardian.api.API;

import static org.apiguardian.api.API.Status.EXPERIMENTAL;

@API(status = EXPERIMENTAL)
public final class TagFlowListener implements FlowListener {

    @Override
    public void onRead(final Span span, final FlowId flowId) {
        switch (flowId.getSource()) {
            case BAGGAGE:
            case HEADER:
                span.setTag(Flow.Tag.FLOW_ID, flowId.getValue());
                break;
            default:
        }
    }

}
