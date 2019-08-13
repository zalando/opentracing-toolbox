package org.zalando.tracer;

import io.opentracing.Span;
import org.zalando.tracer.FlowId.Source;

import java.util.Optional;
import java.util.function.UnaryOperator;

final class TraceExtractor implements Extractor {

    @Override
    public Optional<FlowId> extract(final Span span, final UnaryOperator<String> reader) {
        return Optional.of(span.context().toTraceId())
                .map(id -> new SimpleFlowId(id, Source.TRACE));
    }

}
