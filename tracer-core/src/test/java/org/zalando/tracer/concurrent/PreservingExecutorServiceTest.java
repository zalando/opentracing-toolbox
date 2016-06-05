package org.zalando.tracer.concurrent;

/*
 * ⁣​
 * Tracer: Core
 * ⁣⁣
 * Copyright (C) 2015 - 2016 Zalando SE
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

import com.google.common.util.concurrent.SettableFuture;
import org.junit.Test;
import org.zalando.tracer.Trace;
import org.zalando.tracer.Tracer;

import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import static java.util.Collections.singletonList;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

public final class PreservingExecutorServiceTest {

    private final ExecutorService executor = Executors.newSingleThreadExecutor();
    private final Tracer tracer = Tracer.create("X-Trace");
    private final ExecutorService unit = TracingExecutors.preserve(executor, tracer);

    @Test
    public void shouldPreserveTraceForSubmitRunnable() throws InterruptedException, ExecutionException, TimeoutException {
        final SettableFuture<String> future = SettableFuture.create();
        final Trace trace = tracer.get("X-Trace");

        tracer.start();
        final String expected = trace.getValue();
        try {
            final Runnable task = () -> future.set(trace.getValue());
            unit.submit(task);
        } finally {
            tracer.stop();
        }

        assertThat(future.get(100, TimeUnit.MILLISECONDS), is(expected));
    }

    @Test
    public void shouldPreserveTraceForSubmitRunnableWithResult()
            throws InterruptedException, ExecutionException, TimeoutException {
        final SettableFuture<String> future = SettableFuture.create();
        final Trace trace = tracer.get("X-Trace");

        tracer.start();
        final String expected = trace.getValue();
        try {
            final Runnable task = () -> future.set(trace.getValue());
            unit.submit(task, "foo");
        } finally {
            tracer.stop();
        }

        assertThat(future.get(100, TimeUnit.MILLISECONDS), is(expected));
    }

    @Test
    public void shouldPreserveTraceForSubmitCallable() throws InterruptedException, ExecutionException, TimeoutException {
        final Future<String> future;
        final Trace trace = tracer.get("X-Trace");

        tracer.start();
        final String expected = trace.getValue();
        try {
            final Callable<String> task = trace::getValue;
            future = unit.submit(task);
        } finally {
            tracer.stop();
        }

        assertThat(future.get(100, TimeUnit.MILLISECONDS), is(expected));
    }

    @Test
    public void shouldPreserveTraceForInvokeAll() throws InterruptedException, ExecutionException, TimeoutException {
        final List<Future<String>> future;
        final Trace trace = tracer.get("X-Trace");

        tracer.start();
        final String expected = trace.getValue();
        try {
            final Callable<String> task = trace::getValue;
            future = unit.invokeAll(singletonList(task));
        } finally {
            tracer.stop();
        }

        assertThat(future.get(0).get(100, TimeUnit.MILLISECONDS), is(expected));
    }

    @Test
    public void shouldPreserveTraceForInvokeAllWithTimeout() throws InterruptedException, ExecutionException, TimeoutException {
        final List<Future<String>> future;
        final Trace trace = tracer.get("X-Trace");

        tracer.start();
        final String expected = trace.getValue();
        try {
            final Callable<String> task = trace::getValue;
            future = unit.invokeAll(singletonList(task), 100, TimeUnit.MILLISECONDS);
        } finally {
            tracer.stop();
        }

        assertThat(future.get(0).get(100, TimeUnit.MILLISECONDS), is(expected));
    }

    @Test
    public void shouldPreserveTraceForInvokeAny() throws InterruptedException, ExecutionException, TimeoutException {
        final Trace trace = tracer.get("X-Trace");

        tracer.start();
        final String actual;
        final String expected = trace.getValue();
        try {
            final Callable<String> task = trace::getValue;
            actual = unit.invokeAny(singletonList(task));
        } finally {
            tracer.stop();
        }

        assertThat(actual, is(expected));
    }

    @Test
    public void shouldPreserveTraceForInvokeAnyWithTimeout() throws InterruptedException, ExecutionException, TimeoutException {
        final Trace trace = tracer.get("X-Trace");

        tracer.start();
        final String actual;
        final String expected = trace.getValue();
        try {
            final Callable<String> task = trace::getValue;
            actual = unit.invokeAny(singletonList(task), 100, TimeUnit.MILLISECONDS);
        } finally {
            tracer.stop();
        }

        assertThat(actual, is(expected));
    }

    @Test
    public void shouldShutdown() throws InterruptedException {
        assertThat(unit.isTerminated(), is(false));
        assertThat(unit.isShutdown(), is(false));

        unit.shutdown();
        unit.shutdownNow();
        unit.awaitTermination(100, TimeUnit.MILLISECONDS);

        assertThat(unit.isTerminated(), is(true));
        assertThat(unit.isShutdown(), is(true));
    }

}
