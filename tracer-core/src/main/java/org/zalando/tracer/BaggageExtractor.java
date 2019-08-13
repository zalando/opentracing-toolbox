package org.zalando.tracer;

import io.opentracing.Span;
import org.zalando.tracer.Flow.Baggage;
import org.zalando.tracer.FlowId.Source;

import java.util.Optional;
import java.util.function.UnaryOperator;

final class BaggageExtractor implements Extractor {

    @Override
    public Optional<FlowId> extract(final Span span, final UnaryOperator<String> reader) {
        return Optional.ofNullable(span.getBaggageItem(Baggage.FLOW_ID))
                .map(id -> new SimpleFlowId(id, Source.BAGGAGE));
    }

}
