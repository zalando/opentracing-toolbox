package org.zalando.tracer.concurrent;

import org.junit.Test;
import org.zalando.tracer.Tracer;

import java.util.concurrent.Executor;

public final class PreservingExecutorTest extends AbstractPreservingExecutorTest {

    @Override
    protected Executor unit(final Executor executor, final Tracer tracer) {
        return TracingExecutors.preserve(executor, tracer);
    }

    @Test(expected = IllegalStateException.class)
    public void shouldRequireActiveTrace() {
        unit(executor, tracer).execute(() -> tracer.get("X-Trace").getValue());
    }

}