package org.zalando.tracer.concurrent;

import org.junit.jupiter.api.Test;
import org.zalando.tracer.Trace;
import org.zalando.tracer.Tracer;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;

final class TryPreservingScheduledExecutorServiceTest extends AbstractPreservingScheduledExecutorServiceTest {

    @Override
    protected ScheduledExecutorService unit(final ScheduledExecutorService executor, final Tracer tracer) {
        return TracingExecutors.tryPreserve(executor, tracer);
    }

    @Test
    void shouldManageTraceForScheduleRunnableIfNotStarted() throws InterruptedException, ExecutionException, TimeoutException {
        final CompletableFuture<String> future = new CompletableFuture<>();
        final Trace trace = tracer.get("X-Trace");

        final Runnable task = () -> future.complete(trace.getValue());
        unit(executor, tracer).schedule(task, 0, TimeUnit.NANOSECONDS);

        assertThat(future.get(1000, TimeUnit.MILLISECONDS), is(notNullValue()));
    }


}
