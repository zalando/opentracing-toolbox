package org.zalando.tracer.concurrent;

import org.junit.Test;
import org.zalando.tracer.Trace;
import org.zalando.tracer.Tracer;

import java.util.List;
import java.util.concurrent.*;

import static java.util.Collections.singletonList;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

public abstract class AbstractPreservingExecutorServiceTest {

    protected final ExecutorService executor = Executors.newSingleThreadExecutor();
    protected final Tracer tracer = Tracer.create("X-Trace");

    protected abstract ExecutorService unit(ExecutorService executor, Tracer tracer);

    @Test
    public void shouldPreserveTraceForSubmitRunnable() throws InterruptedException, ExecutionException, TimeoutException {
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
    public void shouldPreserveTraceForSubmitRunnableWithResult()
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
    public void shouldPreserveTraceForSubmitCallable() throws InterruptedException, ExecutionException, TimeoutException {
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
    public void shouldPreserveTraceForInvokeAll() throws InterruptedException, ExecutionException, TimeoutException {
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
    public void shouldPreserveTraceForInvokeAllWithTimeout() throws InterruptedException, ExecutionException, TimeoutException {
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
    public void shouldPreserveTraceForInvokeAny() throws InterruptedException, ExecutionException, TimeoutException {
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
    public void shouldPreserveTraceForInvokeAnyWithTimeout() throws InterruptedException, ExecutionException, TimeoutException {
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
    public void shouldShutdown() throws InterruptedException {
        assertThat(unit(executor, tracer).isTerminated(), is(false));
        assertThat(unit(executor, tracer).isShutdown(), is(false));

        unit(executor, tracer).shutdown();
        unit(executor, tracer).shutdownNow();
        unit(executor, tracer).awaitTermination(1000, TimeUnit.MILLISECONDS);

        assertThat(unit(executor, tracer).isTerminated(), is(true));
        assertThat(unit(executor, tracer).isShutdown(), is(true));
    }

}
