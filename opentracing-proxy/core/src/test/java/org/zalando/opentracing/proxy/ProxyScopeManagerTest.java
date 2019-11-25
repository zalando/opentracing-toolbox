package org.zalando.opentracing.proxy;

import io.opentracing.Scope;
import io.opentracing.ScopeManager;
import io.opentracing.Span;
import io.opentracing.Tracer;
import io.opentracing.mock.MockSpan;
import io.opentracing.mock.MockTracer;
import org.junit.jupiter.api.Test;

import static com.google.common.collect.Iterables.getOnlyElement;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.zalando.opentracing.proxy.Naming.naming;

class ProxyScopeManagerTest {

    private final MockTracer tracer = new MockTracer();
    private final Tracer unit = new ProxyTracer(tracer)
            .with(naming(String::toLowerCase));

    @Test
    void shouldProxyActiveSpan() {
        final Span test = unit.buildSpan("test").start();
        try (final Scope ignored = unit.activateSpan(test)) {
            unit.activeSpan().setOperationName("GET");
        } finally {
            test.finish();
        }

        final MockSpan span = getOnlyElement(tracer.finishedSpans());
        assertEquals("get", span.operationName());
    }

    @Test
    void shouldNotProxyActiveNullSpan() {
        try (final Scope ignored = unit.activateSpan(null)) {
            assertNull(unit.activeSpan());
        }
    }

    @Test
    void shouldProxyScopeManager() {
        final ScopeManager manager = unit.scopeManager();

        final Span test = unit.buildSpan("test").start();
        try (final Scope ignored = manager.activate(test)) {
            manager.activeSpan().setOperationName("GET");
        } finally {
            test.finish();
        }

        final MockSpan span = getOnlyElement(tracer.finishedSpans());
        assertEquals("get", span.operationName());
    }

    @Test
    void shouldNotProxyScopeManagerNullSpanns() {
        try (final Scope ignored = unit.scopeManager().activate(null)) {
            assertNull(unit.activeSpan());
        }
    }

}
