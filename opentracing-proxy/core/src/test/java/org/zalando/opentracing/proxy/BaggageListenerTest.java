package org.zalando.opentracing.proxy;

import io.opentracing.Span;
import io.opentracing.Tracer;
import io.opentracing.mock.MockTracer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.zalando.opentracing.proxy.BaggageListener.baggage;

class BaggageListenerTest {

    @SuppressWarnings("Convert2Lambda")
    private final BaggageListener listener = spy(new BaggageListener() {
        @Override
        public void onBaggage(final Span span, final String key, final String value) {
            // nothing to do
        }
    });

    private final Tracer unit = new ProxyTracer(new MockTracer())
            .with(baggage(listener));

    @AfterEach
    void tearDown() {
        unit.close();
    }

    @Test
    void shouldDelegateOnBaggage() {
        final Span span = unit.buildSpan("test").start()
                .setBaggageItem("k", "v");

        verify(listener).onBaggage(span, "k", "v");
    }

}
