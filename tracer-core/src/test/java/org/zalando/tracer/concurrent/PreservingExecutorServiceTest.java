package org.zalando.tracer.concurrent;

import org.zalando.tracer.Tracer;

import java.util.concurrent.ExecutorService;

final class PreservingExecutorServiceTest extends AbstractPreservingExecutorServiceTest {

    private final ExecutorService unit = TracingExecutors.preserve(executor, tracer);

    @Override
    protected ExecutorService unit(ExecutorService executor, Tracer tracer) {
        return unit;
    }

}
