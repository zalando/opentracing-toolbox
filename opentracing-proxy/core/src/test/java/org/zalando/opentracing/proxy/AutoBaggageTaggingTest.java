package org.zalando.opentracing.proxy;

import io.opentracing.Tracer;
import io.opentracing.mock.MockSpan;
import io.opentracing.mock.MockTracer;
import org.junit.jupiter.api.Test;

import static com.google.common.collect.Iterables.getOnlyElement;
import static org.junit.jupiter.api.Assertions.assertEquals;

class AutoBaggageTaggingTest {

    private final MockTracer tracer = new MockTracer();
    private final Tracer unit = new ProxyTracer(tracer)
            .with(new AutoBaggage("special"))
            .with(new AutoTagging("special"));

    @Test
    void shouldTag() {
        unit.buildSpan("test").start()
                .setTag("special", "yes")
                .finish();

        final MockSpan span = getOnlyElement(tracer.finishedSpans());

        assertEquals("yes", span.tags().get("special"));
        assertEquals("yes", span.getBaggageItem("special"));
    }

    @Test
    void shouldBag() {
        unit.buildSpan("test").start()
                .setBaggageItem("special", "yes")
                .finish();

        final MockSpan span = getOnlyElement(tracer.finishedSpans());

        assertEquals("yes", span.tags().get("special"));
        assertEquals("yes", span.getBaggageItem("special"));
    }

}
