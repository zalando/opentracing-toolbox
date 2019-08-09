package org.zalando.tracer;

import io.opentracing.Span;

import java.util.Optional;
import java.util.function.UnaryOperator;

interface Extractor {

    default Optional<FlowId> extract(final Span span) {
        return extract(span, unused -> null);
    }

    Optional<FlowId> extract(Span span, UnaryOperator<String> reader);

}
