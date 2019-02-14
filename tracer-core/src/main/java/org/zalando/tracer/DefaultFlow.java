package org.zalando.tracer;

import io.opentracing.Span;
import io.opentracing.Tracer;
import lombok.extern.slf4j.Slf4j;

import javax.annotation.Nullable;
import java.util.function.BiConsumer;
import java.util.function.Function;

@Slf4j
final class DefaultFlow implements Flow {

    private final Tracer tracer;

    DefaultFlow(final Tracer tracer) {
        this.tracer = tracer;
    }

    @Override
    public void readFrom(final Function<String, String> reader) {
        final Span span = activeSpan();

        @Nullable final String header = reader.apply(Header.FLOW_ID);

        if (header == null) {
            return;
        }

        @Nullable final String baggage = span.getBaggageItem(Baggage.FLOW_ID);

        if (baggage == null) {
            final String traceId = span.context().toTraceId();

            if (!traceId.equals(header)) {
                log.debug("Received {} header ({}); keeping it as {} baggage",
                        Header.FLOW_ID, header, Baggage.FLOW_ID);
                span.setBaggageItem(Baggage.FLOW_ID, header);
            }

            return;
        }

        if (!header.equals(baggage)) {
            log.warn("{} header ({}) and {} baggage ({}) differ; using baggage",
                    Header.FLOW_ID, header, Baggage.FLOW_ID, baggage);
        }
    }

    @Override
    public String currentId() {
        final Span span = activeSpan();

        @Nullable final String baggage = span.getBaggageItem(Baggage.FLOW_ID);

        if (baggage == null) {
            return span.context().toTraceId();
        }

        return baggage;
    }

    @Override
    public void writeTo(final BiConsumer<String, String> writer) {
        writer.accept(Header.FLOW_ID, currentId());
    }

    private Span activeSpan() {
        @Nullable final Span span = tracer.activeSpan();

        if (span == null) {
            throw new IllegalStateException("No active span found");
        }

        return span;
    }

}
