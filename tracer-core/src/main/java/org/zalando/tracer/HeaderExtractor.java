package org.zalando.tracer;

import io.opentracing.Span;
import org.zalando.tracer.Flow.Header;

import java.util.Optional;
import java.util.function.UnaryOperator;

final class HeaderExtractor implements Extractor {

    @Override
    public Optional<String> extract(final Span span, final UnaryOperator<String> reader) {
        return Optional.ofNullable(reader.apply(Header.FLOW_ID));
    }

}
