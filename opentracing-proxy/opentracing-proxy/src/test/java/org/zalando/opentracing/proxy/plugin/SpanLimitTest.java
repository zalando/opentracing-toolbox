package org.zalando.opentracing.proxy.plugin;

import io.opentracing.Span;
import io.opentracing.Tracer;
import io.opentracing.mock.MockTracer;
import io.opentracing.propagation.TextMapAdapter;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.zalando.opentracing.proxy.core.ProxyTracer;

import java.time.Duration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static io.opentracing.propagation.Format.Builtin.TEXT_MAP;
import static java.util.Collections.emptyMap;
import static java.util.stream.Collectors.toList;
import static java.util.stream.IntStream.range;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class SpanLimitTest {

    private final Tracer unit = new ProxyTracer(new MockTracer())
            .with(new SpanLimit(5));

    @Test
    void allowsSpansBelowLimit() {
        verifyAllows();
    }

    @Test
    void allowsSpansUpToLimit() {
        createSpans(4);
        verifyAllows();
    }

    @Test
    void skipsSpansAboveLimit() {
        createSpans(5);
        verifySkips();
    }

    @Test
    void allowsSpansOnceAgainWhenSpanIsFinished() {
        createSpans(5).get(4).finish();
        verifyAllows();
    }

    @Test
    void allowsSpansWhenSpansAreGarbageCollected() throws InterruptedException {
        createSpans(5);
        System.gc();
        waitFor(Duration.ofSeconds(1));
        verifyAllows();
    }

    @Test
    void injectsSpanContextWhenBelowLimit() {
        final Span span = newSpan();
        assertThat(inject(span), is(not(emptyMap())));
    }

    @Test
    void skipsInjectionWhenAboveLimit() {
        createSpans(5);
        final Span span = newSpan();
        assertTrue(inject(span).isEmpty());
    }

    private Map<String, String> inject(final Span span) {
        final Map<String, String> map = new HashMap<>();
        unit.inject(span.context(), TEXT_MAP, new TextMapAdapter(map));
        return map;
    }

    private List<Span> createSpans(final int amount) {
        return range(0, amount).mapToObj(i -> newSpan()).collect(toList());
    }

    private void verifyAllows() {
        final Span span = newSpan();
        span.setBaggageItem("key", "value");
        assertEquals("value", span.getBaggageItem("key"));
    }

    private void verifySkips() {
        final Span span = newSpan();
        span.setBaggageItem("key", "value");
        assertNull(span.getBaggageItem("key"));
    }

    private Span newSpan() {
        return unit.buildSpan("test").start();
    }

    @SneakyThrows
    private void waitFor(final Duration duration) {
        // not exactly sure why this is needed, but it is for some reason
        Thread.sleep(duration.toMillis());
    }

}
