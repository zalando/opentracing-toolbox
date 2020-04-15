package org.zalando.opentracing.proxy.plugin;

import com.google.common.collect.ImmutableMap;
import io.opentracing.Scope;
import io.opentracing.Span;
import io.opentracing.SpanContext;
import io.opentracing.Tracer;
import io.opentracing.mock.MockTracer;
import io.opentracing.propagation.TextMapAdapter;
import io.opentracing.propagation.TextMapExtractAdapter;
import org.junit.jupiter.api.Test;
import org.slf4j.MDC;
import org.zalando.opentracing.proxy.base.ForwardingSpan;
import org.zalando.opentracing.proxy.core.ProxyTracer;

import java.util.HashMap;

import static io.opentracing.References.FOLLOWS_FROM;
import static io.opentracing.propagation.Format.Builtin.TEXT_MAP_EXTRACT;
import static io.opentracing.propagation.Format.Builtin.TEXT_MAP_INJECT;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class LogCorrelationTest {

    private final MockTracer tracer = new MockTracer();
    private final Tracer unit = new ProxyTracer(tracer)
            .with(new LogCorrelation()
                    .withTraceId("trace_id")
                    .withSpanId("span_id")
                    .withBaggage("flow_id")
                    .withBaggage("request-id", "request_id"));

    @Test
    void correlatesTraceId() {
        final Span span = unit.buildSpan("test").start();

        try (final Scope ignored = unit.activateSpan(span)) {
            assertEquals(span.context().toTraceId(), MDC.get("trace_id"));
        }
    }

    @Test
    void correlatesSpanId() {
        final Span span = unit.buildSpan("test").start();

        try (final Scope ignored = unit.activateSpan(span)) {
            assertEquals(span.context().toSpanId(), MDC.get("span_id"));
        }
    }

    @Test
    void correlatesInitialBaggage() {
        final Span span = unit.buildSpan("test")
                .asChildOf(unit.buildSpan("parent")
                        .asChildOf(context("flow_id", "REcCvlqMSReeo7adheiYFA"))
                        .start())
                .ignoreActiveSpan()
                .withStartTimestamp(0L)
                .addReference(FOLLOWS_FROM, context("x", "y"))
                .start();

        try (final Scope ignored = unit.activateSpan(span)) {
            assertEquals("REcCvlqMSReeo7adheiYFA", MDC.get("flow_id"));
        }
    }

    @Test
    void correlatesNewBaggage() {
        final Span span = unit.buildSpan("test").start();

        try (final Scope ignored = unit.activateSpan(span)) {
            span.setBaggageItem("request-id", "P6QQkzZHfza9GO");
            assertEquals("P6QQkzZHfza9GO", MDC.get("request_id"));
        }
    }

    @Test
    void shouldCleanup() {
        final TextMapAdapter carrier = new TextMapAdapter(new HashMap<>());
        unit.inject(context("request-id", "okPur4VJWZiKzA"),
                TEXT_MAP_INJECT, carrier);

        final Span span = unit.buildSpan("test")
                .asChildOf(unit.extract(TEXT_MAP_EXTRACT, carrier))
                .start();

        unit.activateSpan(span).close();

        assertNull(MDC.get("trace_id"));
        assertNull(MDC.get("span_id"));
        assertNull(MDC.get("request-id"));
    }

    @Test
    void doesntCorrelateWithoutScope() {
        final Span span = unit.buildSpan("test").start();

        span.setBaggageItem("request-id", "P6QQkzZHfza9GO");

        assertNull(MDC.get("request_id"));
    }

    @Test
    void doesntCorrelateWhenSpanIsNotActive() {
        final Span parent = unit.buildSpan("parent").start();

        try (final Scope ignored = unit.activateSpan(parent)) {
            final Span child = unit.buildSpan("child").start();
            child.setBaggageItem("request-id", "P6QQkzZHfza9GO");
            assertNull(MDC.get("request_id"));
        }
    }

    @Test
    void correlatesBaggageWhenSpanIsForwarding() {
        final Span span = unit.buildSpan("test").start();
        final ForwardingSpan wrapped = () -> span;

        try (final Scope ignored = unit.activateSpan(wrapped)) {
            span.setBaggageItem("request-id", "P6QQkzZHfza9GO");
            assertEquals("P6QQkzZHfza9GO", MDC.get("request_id"));
        }
    }

    private SpanContext context(final String key, final String value) {
        return unit.extract(TEXT_MAP_EXTRACT,
                new TextMapExtractAdapter(ImmutableMap.of(
                        "traceid", "1",
                        "spanid", "1",
                        "baggage-" + key, value
                )));
    }

}
