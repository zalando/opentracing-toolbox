package org.zalando.tracer;

import org.junit.jupiter.api.Test;

import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicReference;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasEntry;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.fail;

abstract class AbstractTracerTest {

    protected abstract Tracer unit();

    @Test
    void shouldStartWithoutProvidedValues() {
        final Tracer tracer = Tracer.create("X-Trace-ID");
        final Trace trace = tracer.get("X-Trace-ID");

        tracer.start();

        assertThat(trace.getValue(), is(notNullValue()));
    }

    @Test
    void shouldStartWithProvidedValues() {
        final Tracer tracer = unit();

        tracer.start(trace -> "foo");

        assertThat(tracer.get("X-Trace-ID").getValue(), is("foo"));
    }

    @Test
    void shouldBeActiveWhenStarted() {
        final Tracer tracer = unit();
        tracer.start();
        assertThat(tracer.isActive(), is(true));
    }

    @Test
    void shouldNotBeActiveWhenNotStarted() {
        assertThat(unit().isActive(), is(false));
    }

    @Test
    void shouldGetTrace() {
        final Tracer tracer = unit();

        final Trace trace = tracer.get("X-Trace-ID");

        assertThat(trace, is(notNullValue()));
    }

    @Test
    void shouldGetCurrentTrace() {
        final Tracer tracer = unit();

        tracer.start();

        final Trace trace = tracer.get("X-Trace-ID");

        assertThat(trace.getName(), is("X-Trace-ID"));
        assertThat(trace.getValue(), is(notNullValue()));
    }

    @Test
    void shouldFailToGetTraceIfUnknown() {
        final Tracer tracer = unit();
        assertThrows(IllegalArgumentException.class, () -> tracer.get("X-Foo"));
    }

    @Test
    void shouldFailToGetCurrentTraceIfNotActive() {
        final Trace trace = unit().get("X-Trace-ID");
        assertThrows(IllegalStateException.class, trace::getValue);
    }

    @Test
    void shouldFailToStopIfNotActive() {
        assertThrows(IllegalStateException.class, unit()::stop);
    }

    @Test
    void shouldIterateAllTraces() {
        final Tracer tracer = unit();

        tracer.start();

        final Map<String, String> snapshot = tracer.snapshot();

        assertThat(snapshot.values(), hasSize(3));
        assertThat(snapshot, hasEntry(equalTo("X-Trace-ID"), notNullValue()));
        assertThat(snapshot, hasEntry(equalTo("X-Request-ID"), notNullValue()));
        assertThat(snapshot, hasEntry(equalTo("X-Foo-ID"), equalTo("foo")));
    }

    @Test
    void shouldFailToIterateAllTracesIfNotActive() {
        final Tracer tracer = unit();
        assertThrows(IllegalStateException.class, () -> tracer.forEach((trace, value) -> {
        }));
    }

    @Test
    void shouldManageRunnable() throws ExecutionException, InterruptedException {
        final Tracer tracer = unit();

        final ExecutorService executor = Executors.newSingleThreadExecutor();

        try {
            final Trace trace = tracer.get("X-Foo-ID");

            final AtomicReference<String> ref = new AtomicReference<>();
            final Runnable runnable = tracer.manage(() -> ref.set(trace.getValue()));
            final Future<?> future = executor.submit(runnable);
            future.get();
            final String result = ref.get();

            assertThat(result, is("foo"));
        } finally {
            executor.shutdownNow();
        }
    }

    @Test
    void shouldManageFailingRunnable() throws InterruptedException {
        final Tracer tracer = unit();

        final CountDownLatch latch = new CountDownLatch(1);
        final ExecutorService executor = Executors.newSingleThreadExecutor();

        try {
            final Runnable thrower = () -> {
                latch.countDown();
                throw new UnsupportedOperationException();
            };

            executor.execute(tracer.manage(thrower));
            latch.await();

            try {
                tracer.stop();
                fail("Expected exception");
            } catch (final IllegalStateException e) {
                // expected
            }

        } finally {
            executor.shutdownNow();
        }
    }

    @Test
    void shouldPreserveRunnable() throws ExecutionException, InterruptedException {
        final Tracer tracer = unit();

        final ExecutorService executor = Executors.newSingleThreadExecutor();

        try {
            tracer.start();
            final Trace trace = tracer.get("X-Foo-ID");

            final AtomicReference<String> ref = new AtomicReference<>();
            final Runnable runnable = tracer.preserve(() -> ref.set(trace.getValue()));
            final Future<?> future = executor.submit(runnable);
            future.get();
            final String result = ref.get();

            assertThat(result, is("foo"));
        } finally {
            tracer.stop();
            executor.shutdownNow();
        }
    }

    @Test
    void shouldManageCallable() throws ExecutionException, InterruptedException {
        final Tracer tracer = unit();

        final ExecutorService executor = Executors.newSingleThreadExecutor();

        try {
            final Trace trace = tracer.get("X-Foo-ID");

            final Callable<String> callable = tracer.manage(trace::getValue);
            final Future<String> future = executor.submit(callable);
            final String result = future.get();

            assertThat(result, is("foo"));
        } finally {
            executor.shutdownNow();
        }
    }

    @Test
    void shouldManageFailingCallable() throws InterruptedException {
        final Tracer tracer = unit();

        final CountDownLatch latch = new CountDownLatch(1);
        final ExecutorService executor = Executors.newSingleThreadExecutor();

        try {
            final Callable<String> callable = tracer.manage(() -> {
                latch.countDown();
                throw new UnsupportedOperationException();
            });

            final Future<String> future = executor.submit(callable);

            try {
                future.get();
                fail("Expected exception");
            } catch (final ExecutionException e) {
                assertThat(e.getCause(), is(instanceOf(UnsupportedOperationException.class)));
            }

            latch.await();

            try {
                tracer.stop();
                fail("Expected exception");
            } catch (final IllegalStateException e) {
                // expected
            }
        } finally {
            executor.shutdownNow();
        }
    }

    @Test
    void shouldPreserveCallable() throws ExecutionException, InterruptedException {
        final Tracer tracer = unit();

        final ExecutorService executor = Executors.newSingleThreadExecutor();

        try {
            tracer.start();
            final Trace trace = tracer.get("X-Foo-ID");

            final Callable<String> callable = tracer.preserve(trace::getValue);
            final Future<String> future = executor.submit(callable);
            final String result = future.get();

            assertThat(result, is("foo"));
        } finally {
            tracer.stop();
            executor.shutdownNow();
        }
    }

    @Test
    void shouldFailToDelegateIfNotDelegatingState() {
        final Tracer tracer = unit();

        final ExecutorService executor = Executors.newSingleThreadExecutor();

        try {
            tracer.start();
            final Trace trace = tracer.get("X-Foo-ID");
            final Future<String> future = executor.submit(trace::getValue);

            final ExecutionException exception = assertThrows(ExecutionException.class, future::get);
            assertThat(exception.getCause(), instanceOf(IllegalStateException.class));
        } finally {
            executor.shutdownNow();
        }
    }
}
