package org.zalando.tracer;

/*
 * ⁣​
 * Tracer
 * ⁣⁣
 * Copyright (C) 2015 Zalando SE
 * ⁣⁣
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * ​⁣
 */

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.InOrder;

import static com.google.common.collect.Iterators.cycle;
import static java.util.Arrays.asList;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.mock;

public final class StackedTracerTest extends AbstractTracerTest {

    private final TraceListener listener = mock(TraceListener.class);

    private final Tracer tracer = Tracer.builder()
            .stacked()
            .traces(asList("X-Trace-ID", "X-Request-ID"))
            .trace("X-Foo-ID", cycle("foo", "bar")::next)
            .listener(listener)
            .build();

    @Override
    protected Tracer unit() {
        return tracer;
    }

    @Test
    public void shouldNotFailToStartWithoutProvidedValuesIfAlreadyStarted() {
        tracer.start();
        tracer.start();
    }

    @Test
    public void shouldNotFailToStartWithProvidedValuesIfAlreadyStarted() {
        tracer.start(trace -> "foo");
        tracer.start(trace -> "bar");
    }

    @Test
    public void shouldKeepPreviousTraceWhenStacking() {
        final Trace trace = tracer.get("X-Trace-ID");

        tracer.start();
        final String previous = trace.getValue();
        tracer.start();
        assertThat(trace.getValue(), is(not(previous)));
        tracer.stop();
        assertThat(trace.getValue(), is(previous));
    }

    @Test
    public void shouldTriggerListenersCorrectlyWhenStacking() {
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

}