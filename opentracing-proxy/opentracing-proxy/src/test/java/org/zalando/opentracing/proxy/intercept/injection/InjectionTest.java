package org.zalando.opentracing.proxy.intercept.injection;

import io.opentracing.Span;
import io.opentracing.SpanContext;
import io.opentracing.Tracer;
import io.opentracing.mock.MockTracer;
import io.opentracing.propagation.TextMapAdapter;
import org.junit.jupiter.api.Test;
import org.zalando.opentracing.proxy.core.ProxyTracer;

import java.util.HashMap;
import java.util.Map;

import static io.opentracing.propagation.Format.Builtin.TEXT_MAP;
import static java.util.Collections.emptyMap;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;

class InjectionTest {

    @Test
    void injects() {
        final Tracer unit = new ProxyTracer(new MockTracer());
        assertThat(injectTextMap(unit), is(not(emptyMap())));
    }

    @Test
    void doesntInject() {
        final Tracer unit = new ProxyTracer(new MockTracer())
                .with(Injection.DISABLE);

        assertThat(injectTextMap(unit), is(emptyMap()));
    }

    @Test
    void overrides() {
        final Tracer unit = new ProxyTracer(new MockTracer())
                .with(Injection.DEFAULT)
                .with(Injection.DISABLE);

        assertThat(injectTextMap(unit), is(emptyMap()));

    }

    private Span newSpan(final Tracer tracer) {
        return tracer.buildSpan("test").start();
    }

    private Map<String, String> injectTextMap(final Tracer unit) {
        final SpanContext context = newSpan(unit).context();
        final Map<String, String> map = new HashMap<>();
        unit.inject(context, TEXT_MAP, new TextMapAdapter(map));
        return map;
    }

}
