package org.zalando.tracer.concurrent;

import org.junit.jupiter.api.Test;
import org.zalando.tracer.Trace;
import org.zalando.tracer.Tracer;

import java.util.concurrent.Callable;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

abstract class AbstractPreservingScheduledExecutorServiceTest {

    protected final ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();
    protected final Tracer tracer = Tracer.create("X-Trace");

    protected abstract ScheduledExecutorService unit(ScheduledExecutorService executor, Tracer tracer);

    @Test
    void shouldPreserveTraceForScheduleRunnable() throws InterruptedException, ExecutionException, TimeoutException {
        final CompletableFuture<String> future = new CompletableFuture<>();
        final Trace trace = tracer.get("X-Trace");

        tracer.start();
        final String expected = trace.getValue();
        try {
            final Runnable task = () -> future.complete(trace.getValue());
            unit(executor, tracer).schedule(task, 0, TimeUnit.NANOSECONDS);
        } finally {
            tracer.stop();
        }

        assertThat(future.get(1000, TimeUnit.MILLISECONDS), is(expected));
    }

    @Test
    void shouldPreserveTraceForScheduleCallable() throws InterruptedException, ExecutionException, TimeoutException {
        final Future<String> future;
        final Trace trace = tracer.get("X-Trace");

        tracer.start();
        final String expected = trace.getValue();
        try {
            final Callable<String> task = trace::getValue;
            future = unit(executor, tracer).schedule(task, 0, TimeUnit.NANOSECONDS);
        } finally {
            tracer.stop();
        }

        assertThat(future.get(1000, TimeUnit.MILLISECONDS), is(expected));
    }

    @Test
    void shouldPreserveTraceForScheduleAtFixedRatee() throws InterruptedException, ExecutionException, TimeoutException {
        final CompletableFuture<String> future = new CompletableFuture<>();
        final Trace trace = tracer.get("X-Trace");

        tracer.start();
        final String expected = trace.getValue();
        try {
            final Runnable task = () -> future.complete(trace.getValue());
            unit(executor, tracer).scheduleAtFixedRate(task, 0, Long.MAX_VALUE, TimeUnit.NANOSECONDS);
        } finally {
            tracer.stop();
        }

        assertThat(future.get(1000, TimeUnit.MILLISECONDS), is(expected));
    }

    @Test
    void shouldPreserveTraceForScheduleWithDelayRatee() throws InterruptedException, ExecutionException, TimeoutException {
        final CompletableFuture<String> future = new CompletableFuture<>();
        final Trace trace = tracer.get("X-Trace");

        tracer.start();
        final String expected = trace.getValue();
        try {
            final Runnable task = () -> future.complete(trace.getValue());
            unit(executor, tracer).scheduleWithFixedDelay(task, 0, Long.MAX_VALUE, TimeUnit.NANOSECONDS);
        } finally {
            tracer.stop();
        }

        assertThat(future.get(1000, TimeUnit.MILLISECONDS), is(expected));
    }
}
