package org.zalando.tracer.concurrent;

import org.junit.Test;
import org.zalando.tracer.Trace;
import org.zalando.tracer.Tracer;

import java.util.concurrent.*;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.assertThat;

public final class TryPreservingExecutorServiceTest extends AbstractPreservingExecutorServiceTest {

    @Override
    protected ExecutorService unit(final ExecutorService executor, final Tracer tracer) {
        return TracingExecutors.tryPreserve(executor, tracer);
    }

    @Test
    public void shouldManageTraceForSubmitRunnableIfNotStart() throws InterruptedException, ExecutionException,
            TimeoutException {
        final CompletableFuture<String> future = new CompletableFuture<>();
        final Trace trace = tracer.get("X-Trace");

        final Runnable task = () -> future.complete(trace.getValue());
        unit(executor, tracer).submit(task);

        assertThat(future.get(1000, TimeUnit.MILLISECONDS), is(notNullValue()));
    }

    @Test
    public void shouldManageTraceForSubmitCallableIfNotStarted() throws InterruptedException, ExecutionException,
            TimeoutException {
        final Future<String> future;
        final Trace trace = tracer.get("X-Trace");

        final Callable<String> task = trace::getValue;
        future = unit(executor, tracer).submit(task);

        assertThat(future.get(1000, TimeUnit.MILLISECONDS), is(notNullValue()));
    }

}
