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
import static org.hamcrest.Matchers.not;

final class ManagingExecutorServiceTest {

    private final ExecutorService executor = Executors.newSingleThreadExecutor();
    private final Tracer tracer = Tracer.create("X-Trace");
    private final ExecutorService unit = TracingExecutors.manage(executor, tracer);

    @Test
    void shouldManageTraceForSubmitRunnable() throws InterruptedException, ExecutionException, TimeoutException {
        final CompletableFuture<String> future = new CompletableFuture<>();
        final Trace trace = tracer.get("X-Trace");

        tracer.start();
        final String expected = trace.getValue();
        try {
            final Runnable task = () -> future.complete(trace.getValue());
            unit.submit(task);
        } finally {
            tracer.stop();
        }

        assertThat(future.get(1000, TimeUnit.MILLISECONDS), is(not(expected)));
    }

    @Test
    void shouldManageTraceForSubmitRunnableWithResult()
            throws InterruptedException, ExecutionException, TimeoutException {
        final CompletableFuture<String> future = new CompletableFuture<>();
        final Trace trace = tracer.get("X-Trace");

        tracer.start();
        final String expected = trace.getValue();
        try {
            final Runnable task = () -> future.complete(trace.getValue());
            unit.submit(task, "foo");
        } finally {
            tracer.stop();
        }

        assertThat(future.get(1000, TimeUnit.MILLISECONDS), is(not(expected)));
    }

    @Test
    void shouldManageTraceForSubmitCallable() throws InterruptedException, ExecutionException, TimeoutException {
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

        assertThat(future.get(1000, TimeUnit.MILLISECONDS), is(not(expected)));
    }

    @Test
    void shouldManageTraceForInvokeAll() throws InterruptedException, ExecutionException, TimeoutException {
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

        assertThat(future.get(0).get(1000, TimeUnit.MILLISECONDS), is(not(expected)));
    }

    @Test
    void shouldManageTraceForInvokeAllWithTimeout() throws InterruptedException, ExecutionException, TimeoutException {
        final List<Future<String>> future;
        final Trace trace = tracer.get("X-Trace");

        tracer.start();
        final String expected = trace.getValue();
        try {
            final Callable<String> task = trace::getValue;
            future = unit.invokeAll(singletonList(task), 1000, TimeUnit.MILLISECONDS);
        } finally {
            tracer.stop();
        }

        assertThat(future.get(0).get(1000, TimeUnit.MILLISECONDS), is(not(expected)));
    }

    @Test
    void shouldManageTraceForInvokeAny() throws InterruptedException, ExecutionException {
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

        assertThat(actual, is(not(expected)));
    }

    @Test
    void shouldManageTraceForInvokeAnyWithTimeout() throws InterruptedException, ExecutionException, TimeoutException {
        final Trace trace = tracer.get("X-Trace");

        tracer.start();
        final String actual;
        final String expected = trace.getValue();
        try {
            final Callable<String> task = trace::getValue;
            actual = unit.invokeAny(singletonList(task), 1000, TimeUnit.MILLISECONDS);
        } finally {
            tracer.stop();
        }

        assertThat(actual, is(not(expected)));
    }

    @Test
    void shouldShutdown() throws InterruptedException {
        assertThat(unit.isTerminated(), is(false));
        assertThat(unit.isShutdown(), is(false));

        unit.shutdown();
        unit.shutdownNow();
        unit.awaitTermination(1000, TimeUnit.MILLISECONDS);

        assertThat(unit.isTerminated(), is(true));
        assertThat(unit.isShutdown(), is(true));
    }

}
