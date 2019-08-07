package org.zalando.tracer;

import io.opentracing.Span;
import io.opentracing.Tracer;
import lombok.extern.slf4j.Slf4j;

import javax.annotation.Nullable;
import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.UnaryOperator;

@Slf4j
final class DefaultFlow implements Flow {

    private final Extractor extractor = new CompositeExtractor(
            new BaggageExtractor(),
            new HeaderExtractor(),
            new TraceExtractor()
    );

    private final Tracer tracer;

    DefaultFlow(final Tracer tracer) {
        this.tracer = tracer;
    }

    @Override
    public void readFrom(final UnaryOperator<String> reader) {
        final Span span = activeSpan();

        final Optional<String> now = extractor.extract(span, reader);
        final Optional<String> later = extractor.extract(span);

        if (now.equals(later)) {
            // We proved that we could extract the same flow id without the given reader,
            // hence we don't need to remember it.
            return;
        }

        now.ifPresent(flowId ->
                span.setBaggageItem(Baggage.FLOW_ID, flowId));
    }

    @Override
    public String currentId() {
        return extractor.extract(activeSpan())
                .orElseThrow(IllegalStateException::new);
    }

    @Override
    public void writeTo(final BiConsumer<String, String> writer) {
        writer.accept(Header.FLOW_ID, currentId());
    }

    @Override
    public <T> T write(final BiFunction<String, String, T> writer) {
        return writer.apply(Header.FLOW_ID, currentId());
    }

    private Span activeSpan() {
        @Nullable final Span span = tracer.activeSpan();

        if (span == null) {
            throw new IllegalStateException("No active span found");
        }

        return span;
    }

}
