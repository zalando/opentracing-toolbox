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

import com.google.common.collect.ImmutableMap;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicReference;

import static java.util.Arrays.asList;
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

    private final Tracer tracer = Tracer.builder()
            .traces(asList("X-Trace-ID", "X-Request-ID"))
            .trace("X-Foo-ID", () -> "foo")
            .build();

    @Test
    public void shouldStartWithoutProvidedValues() {
        final Tracer tracer = Tracer.create("X-Trace-ID");
        final Trace trace = tracer.get("X-Trace-ID");

        tracer.start();

        assertThat(trace.getValue(), is(notNullValue()));
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

        assertThat(trace.getName(), is("X-Trace-ID"));
        assertThat(trace.getValue(), is(notNullValue()));
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldFailToGetTraceIfUnknown() {
        tracer.get("X-Foo");
    }

    @Test(expected = IllegalStateException.class)
    public void shouldFailToGetCurrentTraceIfNotActive() {
        tracer.get("X-Trace-ID").getValue();
    }

    @Test
    public void shouldIterateAllTraces() {
        tracer.start();

        final ImmutableMap<String, String> snapshot = tracer.snapshot();

        assertThat(snapshot.values(), hasSize(3));
        assertThat(snapshot, hasEntry(equalTo("X-Trace-ID"), notNullValue()));
        assertThat(snapshot, hasEntry(equalTo("X-Request-ID"), notNullValue()));
        assertThat(snapshot, hasEntry(equalTo("X-Foo-ID"), equalTo("foo")));
    }

    @Test(expected = IllegalStateException.class)
    public void shouldFailToIterateAllTracesIfNotActive() {
        tracer.forEach((trace, value) -> {
        });
    }

    @Test
    public void shouldManageRunnable() throws ExecutionException, InterruptedException {
        final ExecutorService executor = Executors.newSingleThreadExecutor();

        try {
            final Trace trace = tracer.get("X-Foo-ID");

            final AtomicReference<String> ref = new AtomicReference<>();
            final Runnable runnable = tracer.manage(() -> ref.set(trace.getValue()));
            final Future<?> future = executor.submit(runnable);
            future.get();
            final String result = ref.get();

            assertThat(result, is("foo"));
        } finally {
            executor.shutdownNow();
        }
    }

    @Test
    public void shouldPreserveRunnable() throws ExecutionException, InterruptedException {
        final ExecutorService executor = Executors.newSingleThreadExecutor();

        try {
            tracer.start();
            final Trace trace = tracer.get("X-Foo-ID");

            final AtomicReference<String> ref = new AtomicReference<>();
            final Runnable runnable = tracer.preserve(() -> ref.set(trace.getValue()));
            final Future<?> future = executor.submit(runnable);
            future.get();
            final String result = ref.get();

            assertThat(result, is("foo"));
        } finally {
            tracer.stop();
            executor.shutdownNow();
        }
    }

    @Test
    public void shouldManageCallable() throws ExecutionException, InterruptedException {
        final ExecutorService executor = Executors.newSingleThreadExecutor();

        try {
            final Trace trace = tracer.get("X-Foo-ID");

            final Callable<String> callable = tracer.manage(trace::getValue);
            final Future<String> future = executor.submit(callable);
            final String result = future.get();

            assertThat(result, is("foo"));
        } finally {
            executor.shutdownNow();
        }
    }

    @Test
    public void shouldPreserveCallable() throws ExecutionException, InterruptedException {
        final ExecutorService executor = Executors.newSingleThreadExecutor();

        try {
            tracer.start();
            final Trace trace = tracer.get("X-Foo-ID");

            final Callable<String> callable = tracer.preserve(trace::getValue);
            final Future<String> future = executor.submit(callable);
            final String result = future.get();

            assertThat(result, is("foo"));
        } finally {
            tracer.stop();
            executor.shutdownNow();
        }
    }

    @Test
    public void shouldFailToDelegateIfNotDelegatingState() throws ExecutionException, InterruptedException {
        final ExecutorService executor = Executors.newSingleThreadExecutor();

        try {
            tracer.start();
            final Trace trace = tracer.get("X-Foo-ID");

            exception.expect(ExecutionException.class);
            exception.expectCause(instanceOf(IllegalStateException.class));

            final Future<String> future = executor.submit(trace::getValue);
            future.get();

        } finally {
            executor.shutdownNow();
        }
    }

}