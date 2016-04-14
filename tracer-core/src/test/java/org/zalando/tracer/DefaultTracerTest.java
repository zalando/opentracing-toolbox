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

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasEntry;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.assertThat;

public final class DefaultTracerTest {

    @Rule
    public final ExpectedException exception = ExpectedException.none();

    private final Tracer tracer = Tracer.create("X-Trace-ID", "X-Request-ID");

    @Test
    public void shouldStartWithoutProvidedValues() {
        tracer.start();

        assertThat(tracer.get("X-Trace-ID").getValue(), is(notNullValue()));
    }

    @Test(expected = IllegalStateException.class)
    public void shouldFailToStartWithoutProvidedValuesIfAlreadyStarted() {
        tracer.start();
        tracer.start();
    }

    @Test
    public void shouldStartWithProvidedValues() {
        tracer.start(trace -> "foo");

        assertThat(tracer.get("X-Trace-ID").getValue(), is("foo"));
    }

    @Test(expected = IllegalStateException.class)
    public void shouldFailToStartWithProvidedValuesIfAlreadyStarted() {
        tracer.start(trace -> "foo");
        tracer.start(trace -> "bar");
    }

    @Test
    public void shouldGetTrace() {
        final Trace trace = tracer.get("X-Trace-ID");

        assertThat(trace, is(notNullValue()));
    }

    @Test
    public void shouldGetCurrentTrace() {
        tracer.start();

        final Trace trace = tracer.get("X-Trace-ID");
        assertThat(trace.getValue(), is(notNullValue()));
    }

    @Test(expected = IllegalStateException.class)
    public void shouldFailToGetTraceIfUnknown() {
        tracer.get("X-Foo");
    }

    @Test(expected = IllegalStateException.class)
    public void shouldFailToGetCurrentTraceIfNotActive() {
        tracer.get("X-Trace-ID").getValue();
    }

    @Test(expected = IllegalStateException.class)
    public void shouldFailToGetCurrentTraceIfStopped() {
        try (Stoppable s = tracer.start()) {
        }
        tracer.get("X-Trace-ID").getValue();
    }

    @Test
    public void shouldIterateAllTraces() {
        tracer.start();

        final Map<String, String> copy = new HashMap<>();
        tracer.forEach(copy::put);

        assertThat(copy.values(), hasSize(2));
        assertThat(copy, hasEntry(equalTo("X-Trace-ID"), notNullValue()));
        assertThat(copy, hasEntry(equalTo("X-Request-ID"), notNullValue()));
    }

    @Test(expected = IllegalStateException.class)
    public void shouldFailToIterateAllTracesIfNotActive() {
        tracer.forEach((trace, value) -> {
        });
    }

    @Test
    public void shouldDelegate() throws ExecutionException, InterruptedException {
        final ExecutorService executor = Executors.newSingleThreadExecutor();

        try {
            tracer.start(trace -> "foo");
            final Trace trace = tracer.get("X-Trace-ID");

            // TODO this API feels rather clunky
            final Callable<String> callable = tracer.delegate(Closure.valueOf(trace::getValue))::run;
            final Future<String> future = executor.submit(callable);
            final String result = future.get();

            assertThat(result, is("foo"));
        } finally {
            executor.shutdownNow();
        }
    }

    @Test
    public void shouldFailToDelegateIfNotDelegatingState() throws ExecutionException, InterruptedException {
        final ExecutorService executor = Executors.newSingleThreadExecutor();

        try {
            tracer.start(trace -> "foo");
            final Trace trace = tracer.get("X-Trace-ID");

            exception.expect(ExecutionException.class);
            exception.expectCause(instanceOf(IllegalStateException.class));

            final Future<String> future = executor.submit(trace::getValue);
            future.get();

        } finally {
            executor.shutdownNow();
        }
    }

}
