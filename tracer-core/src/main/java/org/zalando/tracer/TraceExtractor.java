package org.zalando.tracer;

import io.opentracing.Span;
import org.zalando.tracer.FlowId.Source;

import java.util.Optional;
import java.util.function.UnaryOperator;

final class TraceExtractor implements Extractor {

    @Override
    public Optional<FlowId> extract(final Span span, final UnaryOperator<String> reader) {
        return Optional.of(span.context().toTraceId())
                // "An empty String will be returned if the tracer does not support this functionality"
                .filter(s -> !s.isEmpty())
                .map(id -> new SimpleFlowId(id, Source.TRACE));
    }

}
