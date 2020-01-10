package org.zalando.opentracing.proxy.intercept.span;

import io.opentracing.Scope;
import io.opentracing.Span;
import io.opentracing.Tracer;
import io.opentracing.Tracer.SpanBuilder;
import io.opentracing.mock.MockSpan;
import io.opentracing.mock.MockTracer;
import org.junit.jupiter.api.Test;
import org.zalando.opentracing.proxy.base.ForwardingSpanBuilder;
import org.zalando.opentracing.proxy.core.ProxyTracer;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;

class SpanBuilderInterceptorTest {

    private final MockTracer tracer = new MockTracer();

    private final Tracer unit = new ProxyTracer(tracer)
            .with((SpanBuilderInterceptor) (tracer, builder) ->
                    new ForwardingSpanBuilder() {
                        @Override
                        public SpanBuilder delegate() {
                            return builder;
                        }

                        @Override
                        public SpanBuilder ignoreActiveSpan() {
                            // disable ability to ignore active span
                            return this;
                        }
                    })
            .with((SpanBuilderInterceptor) (tracer, builder) ->
                    new ForwardingSpanBuilder() {
                        @Override
                        public SpanBuilder delegate() {
                            return builder;
                        }

                        @Override
                        public SpanBuilder withStartTimestamp(final long microseconds) {
                            builder.withStartTimestamp(microseconds + 1);
                            return this;
                        }
                    });

    @Test
    void chainsMultipleInterceptors() {
        {
            final Span parent = unit.buildSpan("parent").start();
            try (final Scope ignored = unit.activateSpan(parent)) {
                unit.buildSpan("child")
                        .ignoreActiveSpan()
                        .withStartTimestamp(1337)
                        .start().finish();
            } finally {
                parent.finish();
            }
        }

        final List<MockSpan> spans = tracer.finishedSpans();

        assertThat(spans, hasSize(2));

        final MockSpan child = spans.get(0);
        assertThat(child.operationName(), is("child"));
        assertThat(child.startMicros(), is(1338L));
        assertThat(child.parentId(), is(greaterThan(0L)));

        final MockSpan parent = spans.get(1);
        assertThat(parent.operationName(), is("parent"));
    }

}
