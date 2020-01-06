package org.zalando.opentracing.proxy;

import io.opentracing.Span;
import io.opentracing.SpanContext;
import io.opentracing.Tracer;
import io.opentracing.mock.MockTracer;
import io.opentracing.propagation.Format;
import io.opentracing.propagation.TextMapAdapter;
import lombok.AllArgsConstructor;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static io.opentracing.propagation.Format.Builtin.BINARY_INJECT;
import static io.opentracing.propagation.Format.Builtin.TEXT_MAP;
import static java.util.Collections.emptyMap;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;

class InjectionTest {

    @AllArgsConstructor
    static class DisableFormat implements Injection {

        private final Format<?> disabled;

        @Override
        public Injector intercept(
                final Injector injector,
                final SpanContext context,
                final Format<?> format) {

            if (format.equals(disabled)) {
                return Injector.NOOP;
            }

            return injector;
        }

    }

    @Test
    void injects() {
        final Tracer unit = disable(BINARY_INJECT);
        assertThat(injectTextMap(unit), is(not(emptyMap())));
    }

    @Test
    void doesntInject() {
        final Tracer unit = disable(TEXT_MAP);
        assertThat(injectTextMap(unit), is(emptyMap()));
    }

    private <C> Tracer disable(final Format<C> format) {
        return new ProxyTracer(new MockTracer())
                .with(new DisableFormat(format));
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
