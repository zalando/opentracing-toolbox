package org.zalando.opentracing.proxy.intercept.baggage;

import com.google.common.collect.ImmutableMap;
import io.opentracing.Span;
import io.opentracing.Tracer;
import io.opentracing.mock.MockSpan;
import io.opentracing.mock.MockTracer;
import lombok.AllArgsConstructor;
import org.junit.jupiter.api.Test;
import org.zalando.opentracing.proxy.core.ProxyTracer;

import java.util.Map;

import static com.google.common.collect.Iterables.getOnlyElement;
import static org.junit.jupiter.api.Assertions.assertEquals;

class BaggageInterceptorTest {

    @AllArgsConstructor
    private static class VersioningBaggageInterceptor implements BaggageInterceptor {

        private final String v2;

        @Override
        public Map<String, String> intercept(final Span span, final String key, final String value) {
            return ImmutableMap.of("version", v2);
        }

    }

    private final MockTracer tracer = new MockTracer();
    private final Tracer unit = new ProxyTracer(tracer)
            .with(BaggageInterceptor.DEFAULT)
            .with(new VersioningBaggageInterceptor("1"))
            .with(new VersioningBaggageInterceptor("2"));

    @Test
    void usesFirstBaggageItemInCaseOfMultipleInterceptorsProvidingTheSameKey() {
        unit.buildSpan("test").start()
                .setBaggageItem("key", "value")
                .finish();

        final MockSpan span = getOnlyElement(tracer.finishedSpans());
        assertEquals("value", span.getBaggageItem("key"));
        assertEquals("1", span.getBaggageItem("version"));
    }

}
