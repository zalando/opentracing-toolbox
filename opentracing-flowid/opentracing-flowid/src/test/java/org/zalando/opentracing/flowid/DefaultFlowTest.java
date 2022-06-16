package org.zalando.opentracing.flowid;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import io.opentracing.Scope;
import io.opentracing.Span;
import io.opentracing.mock.MockSpan;
import io.opentracing.mock.MockTracer;
import static java.util.Collections.*;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import org.zalando.opentracing.flowid.Flow.Baggage;
import org.zalando.opentracing.flowid.Flow.Header;
import org.zalando.opentracing.flowid.Flow.Tag;

class DefaultFlowTest {

    private final MockTracer tracer = new MockTracer();
    private final Flow unit = Flow.create(tracer);

    @Test
    void shouldReadFlowIdFromHeader() {
        final MockSpan span = tracer.buildSpan("test").start();

        try (final Scope ignored = tracer.activateSpan(span)) {
            unit.readFrom(singletonMap(Header.FLOW_ID, "REcCvlqMSReeo7adheiYFA")::get);

            assertEquals("REcCvlqMSReeo7adheiYFA", unit.currentId());
            assertEquals("REcCvlqMSReeo7adheiYFA", span.getBaggageItem(Baggage.FLOW_ID));
            assertEquals("REcCvlqMSReeo7adheiYFA", span.tags().get(Tag.FLOW_ID));
        }
    }

    @Test
    void shouldReadFlowIdFromBaggage() {
        final MockSpan span = tracer.buildSpan("test").start();
        span.setBaggageItem(Baggage.FLOW_ID, "REcCvlqMSReeo7adheiYFA");

        try (final Scope ignored = tracer.activateSpan(span)) {
            unit.readFrom(name -> null);

            assertEquals("REcCvlqMSReeo7adheiYFA", unit.currentId());
            assertEquals("REcCvlqMSReeo7adheiYFA", span.getBaggageItem(Baggage.FLOW_ID));
            assertEquals("REcCvlqMSReeo7adheiYFA", span.tags().get(Tag.FLOW_ID));
        }
    }

    @Test
    void shouldUseTraceIdAsFlowId() {
        final MockSpan span = tracer.buildSpan("test").start();

        try (final Scope ignored = tracer.activateSpan(span)) {
            unit.readFrom(name -> null);

            assertEquals(span.context().toTraceId(), unit.currentId());
            assertNull(span.getBaggageItem(Baggage.FLOW_ID));
            assertFalse(span.tags().containsKey(Tag.FLOW_ID));
        }
    }

    @Test
    void shouldPreferBaggageOverHeaderIfDiffer() {
        final MockSpan span = tracer.buildSpan("test").start();
        span.setBaggageItem(Baggage.FLOW_ID, "REcCvlqMSReeo7adheiYFA");

        try (final Scope ignored = tracer.activateSpan(span)) {
            unit.readFrom(singletonMap(Header.FLOW_ID, "Rso72qSgLWPNlYIF_OGjvA")::get);

            assertEquals("REcCvlqMSReeo7adheiYFA", unit.currentId());
            assertEquals("REcCvlqMSReeo7adheiYFA", span.getBaggageItem(Baggage.FLOW_ID));
            assertEquals("REcCvlqMSReeo7adheiYFA", span.tags().get(Tag.FLOW_ID));
        }
    }

    @Test
    void shouldSupportSameFlowIdInHeaderAndTraceId() {
        final MockSpan span = tracer.buildSpan("test").start();

        try (final Scope ignored = tracer.activateSpan(span)) {
            final String traceId = span.context().toTraceId();
            unit.readFrom(singletonMap(Header.FLOW_ID, traceId)::get);

            assertEquals(traceId, unit.currentId());
            assertNull(span.getBaggageItem(Baggage.FLOW_ID));
            assertFalse(span.tags().containsKey(Tag.FLOW_ID));
        }
    }

    @Test
    void shouldSupportSameFlowIdInHeaderAndBaggage() {
        final MockSpan span = tracer.buildSpan("test").start();
        span.setBaggageItem(Baggage.FLOW_ID, "REcCvlqMSReeo7adheiYFA");

        try (final Scope ignored = tracer.activateSpan(span)) {
            unit.readFrom(singletonMap(Header.FLOW_ID, "REcCvlqMSReeo7adheiYFA")::get);

            assertEquals("REcCvlqMSReeo7adheiYFA", unit.currentId());
            assertEquals("REcCvlqMSReeo7adheiYFA", span.getBaggageItem(Baggage.FLOW_ID));
            assertEquals("REcCvlqMSReeo7adheiYFA", span.tags().get(Tag.FLOW_ID));
        }
    }

    @Test
    void shouldWriteTo() {
        final Span span = tracer.buildSpan("test").start();

        try (final Scope ignored = tracer.activateSpan(span)) {
            unit.readFrom(name -> null);

            final Map<String, String> target = new HashMap<>();
            unit.writeTo(target::put);


            assertEquals(target.get(Header.FLOW_ID), unit.currentId());
        }
    }

    @Test
    void shouldWrite() {
        final Span span = tracer.buildSpan("test").start();

        try (final Scope ignored = tracer.activateSpan(span)) {
            unit.readFrom(name -> null);

            final Map<String, String> target = unit.write(Collections::singletonMap);

            assertEquals(target.get(Header.FLOW_ID), unit.currentId());
        }
    }

    @Test
    void shouldReturnNullWithoutActiveSpan() {
        assertNull(unit.currentId());
    }

}
