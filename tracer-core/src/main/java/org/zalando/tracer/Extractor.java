package org.zalando.tracer;

import io.opentracing.Span;

import java.util.Optional;
import java.util.function.UnaryOperator;

interface Extractor {

    default Optional<String> extract(final Span span) {
        return extract(span, unused -> null);
    }

    Optional<String> extract(Span span, UnaryOperator<String> reader);

}
