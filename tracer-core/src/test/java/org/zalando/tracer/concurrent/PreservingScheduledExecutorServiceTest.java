package org.zalando.tracer.concurrent;

import com.google.common.util.concurrent.SettableFuture;
import org.junit.Test;
import org.zalando.tracer.Trace;
import org.zalando.tracer.Tracer;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.junit.Assert.assertThat;

public final class PreservingScheduledExecutorServiceTest {

    private final ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();
    private final Tracer tracer = Tracer.create("X-Trace");
    private final ScheduledExecutorService unit = TracingExecutors.preserve(executor, tracer);

    @Test
    public void shouldPreserveTraceForScheduleRunnable() throws InterruptedException, ExecutionException, TimeoutException {
        final SettableFuture<String> future = SettableFuture.create();
        final Trace trace = tracer.get("X-Trace");

        tracer.start();
        final String expected = trace.getValue();
        try {
            final Runnable task = () -> future.set(trace.getValue());
            unit.schedule(task, 0, TimeUnit.NANOSECONDS);
        } finally {
            tracer.stop();
        }

        assertThat(future.get(1000, TimeUnit.MILLISECONDS), is(expected));
    }

    @Test
    public void shouldPreserveTraceForScheduleCallable() throws InterruptedException, ExecutionException, TimeoutException {
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

        assertThat(future.get(1000, TimeUnit.MILLISECONDS), is(expected));
    }

    @Test
    public void shouldPreserveTraceForScheduleAtFixedRatee() throws InterruptedException, ExecutionException, TimeoutException {
        final SettableFuture<String> future = SettableFuture.create();
        final Trace trace = tracer.get("X-Trace");

        tracer.start();
        final String expected = trace.getValue();
        try {
            final Runnable task = () -> future.set(trace.getValue());
            unit.scheduleAtFixedRate(task, 0, Long.MAX_VALUE, TimeUnit.NANOSECONDS);
        } finally {
            tracer.stop();
        }

        assertThat(future.get(1000, TimeUnit.MILLISECONDS), is(expected));
    }

    @Test
    public void shouldPreserveTraceForScheduleWithDelayRatee() throws InterruptedException, ExecutionException, TimeoutException {
        final SettableFuture<String> future = SettableFuture.create();
        final Trace trace = tracer.get("X-Trace");

        tracer.start();
        final String expected = trace.getValue();
        try {
            final Runnable task = () -> future.set(trace.getValue());
            unit.scheduleWithFixedDelay(task, 0, Long.MAX_VALUE, TimeUnit.NANOSECONDS);
        } finally {
            tracer.stop();
        }

        assertThat(future.get(1000, TimeUnit.MILLISECONDS), is(expected));
    }

}
