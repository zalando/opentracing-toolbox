package org.zalando.tracer.concurrent;

import com.google.common.util.concurrent.SettableFuture;
import org.junit.Test;
import org.zalando.tracer.Trace;
import org.zalando.tracer.Tracer;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

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
        final SettableFuture<String> future = SettableFuture.create();
        final Trace trace = tracer.get("X-Trace");

        final Runnable task = () -> future.set(trace.getValue());
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
