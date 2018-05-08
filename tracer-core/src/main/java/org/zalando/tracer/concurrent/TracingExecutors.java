package org.zalando.tracer.concurrent;

import org.apiguardian.api.API;
import org.zalando.tracer.Tracer;

import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

import static org.apiguardian.api.API.Status.STABLE;

/**
 * @see Executors
 */
@API(status = STABLE)
public final class TracingExecutors {

    private TracingExecutors() {
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

    public static Executor tryPreserve(final Executor executor, final Tracer tracer) {
        return new TryPreservingExecutor(executor, tracer);
    }

    public static ExecutorService tryPreserve(final ExecutorService executor, final Tracer tracer) {
        return new TryPreservingExecutorService(executor, tracer);
    }

    public static ScheduledExecutorService tryPreserve(final ScheduledExecutorService executor, final Tracer tracer) {
        return new TryPreservingScheduledExecutorService(executor, tracer);
    }

}
