package org.zalando.tracer;

import io.opentracing.Span;
import lombok.AllArgsConstructor;

import java.util.Arrays;
import java.util.Collection;
import java.util.Optional;
import java.util.function.UnaryOperator;

import static java.util.function.Function.identity;

@AllArgsConstructor
final class CompositeExtractor implements Extractor {

    private final Collection<Extractor> strategies;

    CompositeExtractor(final Extractor... strategies) {
        this(Arrays.asList(strategies));
    }

    @Override
    public Optional<FlowId> extract(final Span span, final UnaryOperator<String> reader) {
        return strategies.stream()
                .map(strategy -> strategy.extract(span, reader))
                .filter(Optional::isPresent)
                .findFirst()
                .flatMap(identity());
    }

}
