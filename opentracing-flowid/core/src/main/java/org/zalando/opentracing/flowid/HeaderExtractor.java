package org.zalando.opentracing.flowid;

import io.opentracing.Span;

import java.util.Optional;
import java.util.function.UnaryOperator;

final class HeaderExtractor implements Extractor {

    @Override
    public Optional<FlowId> extract(final Span span, final UnaryOperator<String> reader) {
        return Optional.ofNullable(reader.apply(Flow.Header.FLOW_ID))
                .map(id -> new SimpleFlowId(id, FlowId.Source.HEADER));
    }

}
