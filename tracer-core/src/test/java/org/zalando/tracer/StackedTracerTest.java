package org.zalando.tracer;

import org.junit.jupiter.api.Test;
import org.mockito.InOrder;

import static java.util.Arrays.asList;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.mock;

final class StackedTracerTest extends AbstractTracerTest {

    private final TraceListener listener = mock(TraceListener.class);
    private final StackedTraceListener stackedListener = mock(StackedTraceListener.class);

    private final Tracer tracer = Tracer.builder()
            .stacked()
            .traces(asList("X-Trace-ID", "X-Request-ID"))
            .trace("X-Foo-ID", asList("foo", "bar").iterator()::next)
            .listener(listener)
            .listener(stackedListener)
            .build();

    @Override
    protected Tracer unit() {
        return tracer;
    }

    @Test
    void shouldNotFailToStartWithoutProvidedValuesIfAlreadyStarted() {
        tracer.start();
        tracer.start();
    }

    @Test
    void shouldNotFailToStartWithProvidedValuesIfAlreadyStarted() {
        tracer.start(trace -> "foo");
        tracer.start(trace -> "bar");
    }

    @Test
    void shouldKeepPreviousTraceWhenStacking() {
        final Trace trace = tracer.get("X-Trace-ID");

        tracer.start();
        final String previous = trace.getValue();
        tracer.start();
        assertThat(trace.getValue(), is(not(previous)));
        tracer.stop();
        assertThat(trace.getValue(), is(previous));
    }

    @Test
    void shouldTriggerListenersCorrectlyWhenStacking() {
        final InOrder inOrder = inOrder(listener);

        tracer.start();
        inOrder.verify(listener).onStart("X-Foo-ID", "foo");
        tracer.start();
        inOrder.verify(listener).onStop("X-Foo-ID", "foo");
        inOrder.verify(listener).onStart("X-Foo-ID", "bar");
        tracer.stop();
        inOrder.verify(listener).onStop("X-Foo-ID", "bar");
        inOrder.verify(listener).onStart("X-Foo-ID", "foo");
        tracer.stop();
        inOrder.verify(listener).onStop("X-Foo-ID", "foo");
    }

    @Test
    void shouldTriggerStackedListenersCorrectlyWhenStacking() {
        final InOrder inOrder = inOrder(stackedListener);

        tracer.start();
        inOrder.verify(stackedListener).onStart("X-Foo-ID", "foo");
        tracer.start();
        inOrder.verify(stackedListener).onStart("X-Foo-ID", "bar");
        tracer.stop();
        inOrder.verify(stackedListener).onStop("X-Foo-ID", "bar");
        tracer.stop();
        inOrder.verify(stackedListener).onStop("X-Foo-ID", "foo");
    }

}
