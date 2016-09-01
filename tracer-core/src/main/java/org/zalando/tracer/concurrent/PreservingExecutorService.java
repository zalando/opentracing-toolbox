package org.zalando.tracer.concurrent;

import org.zalando.tracer.Tracer;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;

final class PreservingExecutorService extends DecoratingExecutorService {

    private final ExecutorService executor;
    private final Tracer tracer;

    PreservingExecutorService(final ExecutorService executor, final Tracer tracer) {
        this.executor = executor;
        this.tracer = tracer;
    }

    @Override
    protected ExecutorService delegate() {
        return executor;
    }

    @Override
    protected Runnable decorate(final Runnable command) {
        return tracer.preserve(command);
    }

    @Override
    protected <T> Callable<T> decorate(final Callable<T> task) {
        return tracer.preserve(task);
    }

}
