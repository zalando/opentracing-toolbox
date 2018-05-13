package org.zalando.tracer.concurrent;

import org.junit.jupiter.api.Test;
import org.zalando.tracer.Trace;
import org.zalando.tracer.Tracer;

import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import static java.util.Collections.singletonList;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

abstract class AbstractPreservingExecutorServiceTest {

    protected final ExecutorService executor = Executors.newSingleThreadExecutor();
    protected final Tracer tracer = Tracer.create("X-Trace");

    protected abstract ExecutorService unit(ExecutorService executor, Tracer tracer);

    @Test
    void shouldPreserveTraceForSubmitRunnable() throws InterruptedException, ExecutionException, TimeoutException {
        final CompletableFuture<String> future = new CompletableFuture<>();
        final Trace trace = tracer.get("X-Trace");

        tracer.start();
        final String expected = trace.getValue();
        try {
            final Runnable task = () -> future.complete(trace.getValue());
            unit(executor, tracer).submit(task);
        } finally {
            tracer.stop();
        }

        assertThat(future.get(1000, TimeUnit.MILLISECONDS), is(expected));
    }

    @Test
    void shouldPreserveTraceForSubmitRunnableWithResult()
            throws InterruptedException, ExecutionException, TimeoutException {
        final CompletableFuture<String> future = new CompletableFuture<>();
        final Trace trace = tracer.get("X-Trace");

        tracer.start();
        final String expected = trace.getValue();
        try {
            final Runnable task = () -> future.complete(trace.getValue());
            unit(executor, tracer).submit(task, "foo");
        } finally {
            tracer.stop();
        }

        assertThat(future.get(1000, TimeUnit.MILLISECONDS), is(expected));
    }

    @Test
    void shouldPreserveTraceForSubmitCallable() throws InterruptedException, ExecutionException, TimeoutException {
        final Future<String> future;
        final Trace trace = tracer.get("X-Trace");

        tracer.start();
        final String expected = trace.getValue();
        try {
            final Callable<String> task = trace::getValue;
            future = unit(executor, tracer).submit(task);
        } finally {
            tracer.stop();
        }

        assertThat(future.get(1000, TimeUnit.MILLISECONDS), is(expected));
    }

    @Test
    void shouldPreserveTraceForInvokeAll() throws InterruptedException, ExecutionException, TimeoutException {
        final List<Future<String>> future;
        final Trace trace = tracer.get("X-Trace");

        tracer.start();
        final String expected = trace.getValue();
        try {
            final Callable<String> task = trace::getValue;
            future = unit(executor, tracer).invokeAll(singletonList(task));
        } finally {
            tracer.stop();
        }

        assertThat(future.get(0).get(1000, TimeUnit.MILLISECONDS), is(expected));
    }

    @Test
    void shouldPreserveTraceForInvokeAllWithTimeout() throws InterruptedException, ExecutionException, TimeoutException {
        final List<Future<String>> future;
        final Trace trace = tracer.get("X-Trace");

        tracer.start();
        final String expected = trace.getValue();
        try {
            final Callable<String> task = trace::getValue;
            future = unit(executor, tracer).invokeAll(singletonList(task), 1000, TimeUnit.MILLISECONDS);
        } finally {
            tracer.stop();
        }

        assertThat(future.get(0).get(1000, TimeUnit.MILLISECONDS), is(expected));
    }

    @Test
    void shouldPreserveTraceForInvokeAny() throws InterruptedException, ExecutionException {
        final Trace trace = tracer.get("X-Trace");

        tracer.start();
        final String actual;
        final String expected = trace.getValue();
        try {
            final Callable<String> task = trace::getValue;
            actual = unit(executor, tracer).invokeAny(singletonList(task));
        } finally {
            tracer.stop();
        }

        assertThat(actual, is(expected));
    }

    @Test
    void shouldPreserveTraceForInvokeAnyWithTimeout() throws InterruptedException, ExecutionException, TimeoutException {
        final Trace trace = tracer.get("X-Trace");

        tracer.start();
        final String actual;
        final String expected = trace.getValue();
        try {
            final Callable<String> task = trace::getValue;
            actual = unit(executor, tracer).invokeAny(singletonList(task), 1000, TimeUnit.MILLISECONDS);
        } finally {
            tracer.stop();
        }

        assertThat(actual, is(expected));
    }

    @Test
    void shouldShutdown() throws InterruptedException {
        assertThat(unit(executor, tracer).isTerminated(), is(false));
        assertThat(unit(executor, tracer).isShutdown(), is(false));

        unit(executor, tracer).shutdown();
        unit(executor, tracer).shutdownNow();
        unit(executor, tracer).awaitTermination(1000, TimeUnit.MILLISECONDS);

        assertThat(unit(executor, tracer).isTerminated(), is(true));
        assertThat(unit(executor, tracer).isShutdown(), is(true));
    }

}
