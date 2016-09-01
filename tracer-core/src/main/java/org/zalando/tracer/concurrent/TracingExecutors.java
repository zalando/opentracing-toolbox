package org.zalando.tracer.concurrent;

import com.google.common.util.concurrent.MoreExecutors;
import org.zalando.tracer.Tracer;

import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

/**
 * @see Executors
 * @see MoreExecutors
 */
public final class TracingExecutors {

    TracingExecutors() {
        // package private so we can trick code coverage
    }

    public static Executor manage(final Executor executor, final Tracer tracer) {
        return new ManagingExecutor(executor, tracer);
    }

    public static ExecutorService manage(final ExecutorService executor, final Tracer tracer) {
        return new ManagingExecutorService(executor, tracer);
    }

    public static ScheduledExecutorService manage(final ScheduledExecutorService executor, final Tracer tracer) {
        return new ManagingScheduledExecutorService(executor, tracer);
    }

    public static Executor preserve(final Executor executor, final Tracer tracer) {
        return new PreservingExecutor(executor, tracer);
    }

    public static ExecutorService preserve(final ExecutorService executor, final Tracer tracer) {
        return new PreservingExecutorService(executor, tracer);
    }

    public static ScheduledExecutorService preserve(final ScheduledExecutorService executor, final Tracer tracer) {
        return new PreservingScheduledExecutorService(executor, tracer);
    }

}
