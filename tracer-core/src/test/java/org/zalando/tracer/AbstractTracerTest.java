package org.zalando.tracer;

import com.google.common.collect.ImmutableMap;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicReference;

import static java.util.Arrays.asList;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasEntry;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.assertThat;

public abstract class AbstractTracerTest {

    @Rule
    public final ExpectedException exception = ExpectedException.none();

    protected abstract Tracer unit();

    @Test
    public void shouldStartWithoutProvidedValues() {
        final Tracer tracer = Tracer.create("X-Trace-ID");
        final Trace trace = tracer.get("X-Trace-ID");

        tracer.start();

        assertThat(trace.getValue(), is(notNullValue()));
    }

    @Test
    public void shouldStartWithProvidedValues() {
        final Tracer tracer = unit();

        tracer.start(trace -> "foo");

        assertThat(tracer.get("X-Trace-ID").getValue(), is("foo"));
    }

    @Test
    public void shouldBeActiveWhenStarted() {
        final Tracer tracer = unit();
        tracer.start();
        assertThat(tracer.isActive(), is(true));
    }

    @Test
    public void shouldNotBeActiveWhenNotStarted() {
        assertThat(unit().isActive(), is(false));
    }

    @Test
    public void shouldGetTrace() {
        final Tracer tracer = unit();

        final Trace trace = tracer.get("X-Trace-ID");

        assertThat(trace, is(notNullValue()));
    }

    @Test
    public void shouldGetCurrentTrace() {
        final Tracer tracer = unit();

        tracer.start();

        final Trace trace = tracer.get("X-Trace-ID");

        assertThat(trace.getName(), is("X-Trace-ID"));
        assertThat(trace.getValue(), is(notNullValue()));
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldFailToGetTraceIfUnknown() {
        final Tracer tracer = unit();

        tracer.get("X-Foo");
    }

    @Test(expected = IllegalStateException.class)
    public void shouldFailToGetCurrentTraceIfNotActive() {
        final Tracer tracer = unit();

        tracer.get("X-Trace-ID").getValue();
    }

    @Test
    public void shouldIterateAllTraces() {
        final Tracer tracer = unit();

        tracer.start();

        final ImmutableMap<String, String> snapshot = tracer.snapshot();

        assertThat(snapshot.values(), hasSize(3));
        assertThat(snapshot, hasEntry(equalTo("X-Trace-ID"), notNullValue()));
        assertThat(snapshot, hasEntry(equalTo("X-Request-ID"), notNullValue()));
        assertThat(snapshot, hasEntry(equalTo("X-Foo-ID"), equalTo("foo")));
    }

    @Test(expected = IllegalStateException.class)
    public void shouldFailToIterateAllTracesIfNotActive() {
        final Tracer tracer = unit();

        tracer.forEach((trace, value) -> {
        });
    }

    @Test
    public void shouldManageRunnable() throws ExecutionException, InterruptedException {
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
    public void shouldPreserveRunnable() throws ExecutionException, InterruptedException {
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
    public void shouldManageCallable() throws ExecutionException, InterruptedException {
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
    public void shouldPreserveCallable() throws ExecutionException, InterruptedException {
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
    public void shouldFailToDelegateIfNotDelegatingState() throws ExecutionException, InterruptedException {
        final Tracer tracer = unit();

        final ExecutorService executor = Executors.newSingleThreadExecutor();

        try {
            tracer.start();
            final Trace trace = tracer.get("X-Foo-ID");

            exception.expect(ExecutionException.class);
            exception.expectCause(instanceOf(IllegalStateException.class));

            final Future<String> future = executor.submit(trace::getValue);
            future.get();

        } finally {
            executor.shutdownNow();
        }
    }
}
