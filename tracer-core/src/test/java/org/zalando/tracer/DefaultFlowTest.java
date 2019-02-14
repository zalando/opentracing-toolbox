package org.zalando.tracer;

import io.opentracing.Scope;
import io.opentracing.Span;
import io.opentracing.Tracer;
import io.opentracing.mock.MockTracer;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static java.util.Collections.singletonMap;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class DefaultFlowTest {

    private final Tracer tracer = new MockTracer();
    private final Flow unit = Flow.create(tracer);

    @Test
    void shouldReadFlowIdFromHeader() {
        final Span span = tracer.buildSpan("test").start();

        try (final Scope ignored = tracer.activateSpan(span)) {
            unit.readFrom(singletonMap(Flow.Header.FLOW_ID, "REcCvlqMSReeo7adheiYFA")::get);

            assertEquals("REcCvlqMSReeo7adheiYFA", unit.currentId());
        }
    }

    @Test
    void shouldReadFlowIdFromBaggage() {
        final Span span = tracer.buildSpan("test").start()
                .setBaggageItem(Flow.Baggage.FLOW_ID, "REcCvlqMSReeo7adheiYFA");

        try (final Scope ignored = tracer.activateSpan(span)) {
            unit.readFrom(name -> null);

            assertEquals("REcCvlqMSReeo7adheiYFA", unit.currentId());
        }
    }

    @Test
    void shouldUseTraceIdAsFlowId() {
        final Span span = tracer.buildSpan("test").start();

        try (final Scope ignored = tracer.activateSpan(span)) {
            unit.readFrom(name -> null);

            assertEquals(span.context().toTraceId(), unit.currentId());
        }
    }

    @Test
    void shouldPreferBaggageOverHeaderIfDiffer() {
        final Span span = tracer.buildSpan("test").start()
                .setBaggageItem(Flow.Baggage.FLOW_ID, "REcCvlqMSReeo7adheiYFA");

        try (final Scope ignored = tracer.activateSpan(span)) {
            unit.readFrom(singletonMap(Flow.Header.FLOW_ID, "Rso72qSgLWPNlYIF_OGjvA")::get);

            assertEquals("REcCvlqMSReeo7adheiYFA", unit.currentId());
        }
    }

    @Test
    void shouldSupportSameFlowIdInHeaderAndTraceId() {
        final Span span = tracer.buildSpan("test").start();

        try (final Scope ignored = tracer.activateSpan(span)) {
            final String traceId = span.context().toTraceId();
            unit.readFrom(singletonMap(Flow.Header.FLOW_ID, traceId)::get);

            assertEquals(traceId, unit.currentId());
        }
    }

    @Test
    void shouldSupportSameFlowIdInHeaderAndBaggage() {
        final Span span = tracer.buildSpan("test").start()
                .setBaggageItem(Flow.Baggage.FLOW_ID, "REcCvlqMSReeo7adheiYFA");

        try (final Scope ignored = tracer.activateSpan(span)) {
            unit.readFrom(singletonMap(Flow.Header.FLOW_ID, "REcCvlqMSReeo7adheiYFA")::get);

            assertEquals("REcCvlqMSReeo7adheiYFA", unit.currentId());
        }
    }

    @Test
    void shouldWriteId() {
        final Span span = tracer.buildSpan("test").start();

        try (final Scope ignored = tracer.activateSpan(span)) {
            unit.readFrom(name -> null);

            final Map<String, String> target = new HashMap<>();
            unit.writeTo(target::put);


            assertEquals(target.get(Flow.Header.FLOW_ID), unit.currentId());
        }
    }

    @Test
    void shouldFailWithoutActiveSpan() {
        assertThrows(IllegalStateException.class, unit::currentId);
    }

}
