package org.zalando.tracer.concurrent;

import org.junit.Test;
import org.zalando.tracer.Trace;
import org.zalando.tracer.Tracer;

import java.util.concurrent.*;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

public abstract class AbstractPreservingExecutorTest {

    protected final Executor executor = Executors.newSingleThreadExecutor();
    protected final Tracer tracer = Tracer.create("X-Trace");

    protected abstract Executor unit(Executor executor, Tracer tracer);

    @Test
    public void shouldPreserveTrace() throws InterruptedException, ExecutionException, TimeoutException {
        final CompletableFuture<String> future = new CompletableFuture<>();
        final Trace trace = tracer.get("X-Trace");

        tracer.start();
        final String expected = trace.getValue();
        try {
            unit(executor, tracer).execute(() -> future.complete(trace.getValue()));
        } finally {
            tracer.stop();
        }

        assertThat(future.get(1000, TimeUnit.MILLISECONDS), is(expected));
    }

}
