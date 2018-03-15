package org.zalando.tracer.concurrent;

import org.junit.jupiter.api.Test;
import org.zalando.tracer.Tracer;

import java.util.concurrent.Executor;

import static org.junit.jupiter.api.Assertions.assertThrows;

final class PreservingExecutorTest extends AbstractPreservingExecutorTest {

    @Override
    protected Executor unit(final Executor executor, final Tracer tracer) {
        return TracingExecutors.preserve(executor, tracer);
    }

    @Test
    void shouldRequireActiveTrace() {
        final Executor executor = unit(this.executor, tracer);
        assertThrows(IllegalStateException.class, () -> executor.execute(() -> {}));
    }

}
