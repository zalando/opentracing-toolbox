package org.zalando.opentracing.proxy.plugin;

import io.opentracing.Tracer;
import io.opentracing.mock.MockSpan;
import io.opentracing.mock.MockTracer;
import org.junit.jupiter.api.Test;
import org.zalando.opentracing.proxy.core.ProxyTracer;
import org.zalando.opentracing.proxy.plugin.AutoBaggage;

import static com.google.common.collect.Iterables.getOnlyElement;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class AutoBaggageTest {

    private final MockTracer tracer = new MockTracer();
    private final Tracer unit = new ProxyTracer(tracer)
            .with(new AutoBaggage("priority"));

    @Test
    void shouldAutoBagSpecialTag() {
        unit.buildSpan("test").start()
                .setTag("priority", 1.0)
                .finish();

        final MockSpan span = getOnlyElement(tracer.finishedSpans());

        assertEquals(1.0, span.tags().get("priority"));
        assertEquals("1.0", span.getBaggageItem("priority"));
    }

    @Test
    void shouldNotAutoBagOrdinaryTag() {
        unit.buildSpan("test").start()
                .setTag("preference", 0.1)
                .finish();

        final MockSpan span = getOnlyElement(tracer.finishedSpans());

        assertEquals(0.1, span.tags().get("preference"));
        assertNull(span.getBaggageItem("preference"));
    }

}
