package org.zalando.opentracing.flowid;

import io.opentracing.Span;
import org.zalando.opentracing.flowid.FlowId.Source;

import java.util.Optional;
import java.util.function.UnaryOperator;

final class BaggageExtractor implements Extractor {

    @Override
    public Optional<FlowId> extract(final Span span, final UnaryOperator<String> reader) {
        return Optional.ofNullable(span.getBaggageItem(Flow.Baggage.FLOW_ID))
                .map(id -> new SimpleFlowId(id, Source.BAGGAGE));
    }

}
