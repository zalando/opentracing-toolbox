package org.zalando.tracer;

import io.opentracing.Span;

import java.util.Optional;
import java.util.function.UnaryOperator;

final class BaggageExtractor implements Extractor {

    @Override
    public Optional<String> extract(final Span span, final UnaryOperator<String> reader) {
        return Optional.ofNullable(span.getBaggageItem(Flow.Baggage.FLOW_ID));
    }

}
