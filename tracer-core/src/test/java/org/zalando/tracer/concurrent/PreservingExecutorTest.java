package org.zalando.tracer.concurrent;

import com.google.common.util.concurrent.SettableFuture;
import org.junit.Test;
import org.zalando.tracer.Trace;
import org.zalando.tracer.Tracer;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

public final class PreservingExecutorTest {

    private final Executor executor = Executors.newSingleThreadExecutor();
    private final Tracer tracer = Tracer.create("X-Trace");
    private final Executor unit = TracingExecutors.preserve(executor, tracer);

    @Test(expected = IllegalStateException.class)
    public void shouldRequireActiveTrace() {
        unit.execute(() -> tracer.get("X-Trace").getValue());
    }

    @Test
    public void shouldPreserveTrace() throws InterruptedException, ExecutionException, TimeoutException {
        final SettableFuture<String> future = SettableFuture.create();
        final Trace trace = tracer.get("X-Trace");

        tracer.start();
        final String expected = trace.getValue();
        try {
            unit.execute(() -> future.set(trace.getValue()));
        } finally {
            tracer.stop();
        }

        assertThat(future.get(1000, TimeUnit.MILLISECONDS), is(expected));
    }

}