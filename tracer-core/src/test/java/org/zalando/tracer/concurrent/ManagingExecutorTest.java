package org.zalando.tracer.concurrent;

import org.junit.jupiter.api.Test;
import org.zalando.tracer.Trace;
import org.zalando.tracer.Tracer;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;

final class ManagingExecutorTest {

    private final Executor executor = Executors.newSingleThreadExecutor();
    private final Tracer tracer = Tracer.create("X-Trace");
    private final Executor unit = TracingExecutors.manage(executor, tracer);

    @Test
    void shouldNotRequireActiveTrace() {
        unit.execute(() -> tracer.get("X-Trace").getValue());
    }

    @Test
    void shouldManageTrace() throws InterruptedException, ExecutionException, TimeoutException {
        final CompletableFuture<String> future = new CompletableFuture<>();
        final Trace trace = tracer.get("X-Trace");

        tracer.start();
        final String expected = trace.getValue();
        try {
            unit.execute(() -> future.complete(trace.getValue()));
        } finally {
            tracer.stop();
        }

        assertThat(future.get(1000, TimeUnit.MILLISECONDS), is(not(expected)));
    }

}
