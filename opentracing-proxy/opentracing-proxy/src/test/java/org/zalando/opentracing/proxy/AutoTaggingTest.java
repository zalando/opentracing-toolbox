package org.zalando.opentracing.proxy;

import com.google.common.collect.ImmutableMap;
import io.opentracing.Span;
import io.opentracing.SpanContext;
import io.opentracing.Tracer;
import io.opentracing.mock.MockSpan;
import io.opentracing.mock.MockTracer;
import io.opentracing.propagation.TextMapExtractAdapter;
import org.junit.jupiter.api.Test;

import static com.google.common.collect.Iterables.getOnlyElement;
import static io.opentracing.propagation.Format.Builtin.TEXT_MAP_EXTRACT;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class AutoTaggingTest {

    private final MockTracer tracer = new MockTracer();
    private final Tracer unit = new ProxyTracer(tracer)
            .with(new AutoTagging("special"));

    @Test
    void shouldAutoTagSpecialKeyOnStart() {
        unit.buildSpan("test").asChildOf(context("special"))
                .start().finish();

        final MockSpan span = getOnlyElement(tracer.finishedSpans());

        assertEquals("yes", span.tags().get("special"));
    }

    @Test
    void shouldNotAutoTagOrdinaryKeyOnStart() {
        unit.buildSpan("test").asChildOf(context("ordinary"))
                .start().finish();

        final MockSpan span = getOnlyElement(tracer.finishedSpans());

        assertNull(span.tags().get("ordinary"));
    }

    @Test
    void shouldAutoTagSpecialKeyOnStartChild() {
        final Span parent = unit.buildSpan("parent").start();
        parent.setBaggageItem("special", "yes");

        unit.buildSpan("child").asChildOf(parent).start().finish();

        final MockSpan span = getOnlyElement(tracer.finishedSpans());
        assertEquals("yes", span.tags().get("special"));
    }

    @Test
    void shouldNotAutoTagOrdinaryKeyOnStartChild() {
        final Span parent = unit.buildSpan("parent").start();
        parent.setBaggageItem("ordinary", "yes");

        unit.buildSpan("child").asChildOf(parent).start().finish();

        final MockSpan span = getOnlyElement(tracer.finishedSpans());
        assertNull(span.tags().get("ordinary"));
    }

    @Test
    void shouldAutoTagSpecialKeyOnSetBaggage() {
        unit.buildSpan("test").asChildOf((Span) null).start()
                .setBaggageItem("special", "yes")
                .finish();

        final MockSpan span = getOnlyElement(tracer.finishedSpans());

        assertEquals("yes", span.tags().get("special"));
    }

    @Test
    void shouldNotAutoTagOrdinaryKeyOnSetBaggage() {
        unit.buildSpan("test").start()
                .setBaggageItem("ordinary", "yes")
                .finish();

        final MockSpan span = getOnlyElement(tracer.finishedSpans());

        assertNull(span.tags().get("ordinary"));
    }

    private SpanContext context(final String baggageKey) {
        return unit.extract(TEXT_MAP_EXTRACT,
                new TextMapExtractAdapter(ImmutableMap.of(
                        "traceid", "1",
                        "spanid", "1",
                        "baggage-" + baggageKey, "yes"
                )));
    }

}
