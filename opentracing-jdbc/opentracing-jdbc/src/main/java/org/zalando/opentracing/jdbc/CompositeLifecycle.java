package org.zalando.opentracing.jdbc;

import io.opentracing.Span;
import io.opentracing.Tracer;
import lombok.AllArgsConstructor;

import java.util.Collection;
import java.util.Optional;

@AllArgsConstructor
final class CompositeLifecycle implements Lifecycle {

    private final Collection<Lifecycle> policies;

    @Override
    public Optional<Span> start(
            final Tracer tracer, final String operationName) {

        return policies.stream()
                .map(policy -> policy.start(tracer, operationName))
                .filter(Optional::isPresent)
                .map(Optional::get)
                .findFirst();
    }

}
