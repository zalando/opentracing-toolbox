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
import static org.hamcrest.Matchers.not;

final class ManagingScheduledExecutorServiceTest {

    private final ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();
    private final Tracer tracer = Tracer.create("X-Trace");
    private final ScheduledExecutorService unit = TracingExecutors.manage(executor, tracer);

    @Test
    void shouldManageTraceForScheduleRunnable() throws InterruptedException, ExecutionException, TimeoutException {
        final CompletableFuture<String> future = new CompletableFuture<>();
        final Trace trace = tracer.get("X-Trace");

        tracer.start();
        final String expected = trace.getValue();
        try {
            final Runnable task = () -> future.complete(trace.getValue());
            unit.schedule(task, 0, TimeUnit.NANOSECONDS);
        } finally {
            tracer.stop();
        }

        assertThat(future.get(1000, TimeUnit.MILLISECONDS), is(not(expected)));
    }

    @Test
    void shouldManageTraceForScheduleCallable() throws InterruptedException, ExecutionException, TimeoutException {
        final Future<String> future;
        final Trace trace = tracer.get("X-Trace");

        tracer.start();
        final String expected = trace.getValue();
        try {
            final Callable<String> task = trace::getValue;
            future = unit.schedule(task, 0, TimeUnit.NANOSECONDS);
        } finally {
            tracer.stop();
        }

        assertThat(future.get(1000, TimeUnit.MILLISECONDS), is(not(expected)));
    }

    @Test
    void shouldManageTraceForScheduleAtFixedRatee() throws InterruptedException, ExecutionException, TimeoutException {
        final CompletableFuture<String> future = new CompletableFuture<>();
        final Trace trace = tracer.get("X-Trace");

        tracer.start();
        final String expected = trace.getValue();
        try {
            final Runnable task = () -> future.complete(trace.getValue());
            unit.scheduleAtFixedRate(task, 0, Long.MAX_VALUE, TimeUnit.NANOSECONDS);
        } finally {
            tracer.stop();
        }

        assertThat(future.get(1000, TimeUnit.MILLISECONDS), is(not(expected)));
    }

    @Test
    void shouldManageTraceForScheduleWithDelayRatee() throws InterruptedException, ExecutionException, TimeoutException {
        final CompletableFuture<String> future = new CompletableFuture<>();
        final Trace trace = tracer.get("X-Trace");

        tracer.start();
        final String expected = trace.getValue();
        try {
            final Runnable task = () -> future.complete(trace.getValue());
            unit.scheduleWithFixedDelay(task, 0, Long.MAX_VALUE, TimeUnit.NANOSECONDS);
        } finally {
            tracer.stop();
        }

        assertThat(future.get(1000, TimeUnit.MILLISECONDS), is(not(expected)));
    }

}
