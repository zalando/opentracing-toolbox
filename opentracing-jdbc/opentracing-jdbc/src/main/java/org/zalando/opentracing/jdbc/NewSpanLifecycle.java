package org.zalando.opentracing.jdbc;

import io.opentracing.Span;
import io.opentracing.Tracer;
import io.opentracing.tag.Tags;
import lombok.AllArgsConstructor;
import org.apiguardian.api.API;

import java.util.Optional;

import static org.apiguardian.api.API.Status.EXPERIMENTAL;

@API(status = EXPERIMENTAL)
@AllArgsConstructor
public final class NewSpanLifecycle implements Lifecycle {

    @Override
    public Optional<Span> start(
            final Tracer tracer, final String operationName) {

        return Optional.of(tracer.buildSpan(operationName)
                .withTag(Tags.SPAN_KIND, Tags.SPAN_KIND_CLIENT)
                .start());
    }

}
