package org.zalando.tracer.concurrent;

import org.zalando.tracer.Tracer;

import java.util.concurrent.Executor;

final class TryPreservingExecutor extends DecoratingExecutor {

    private final Executor executor;
    private final Tracer tracer;

    TryPreservingExecutor(final Executor executor, final Tracer tracer) {
        this.executor = executor;
        this.tracer = tracer;
    }

    @Override
    protected Executor delegate() {
        return executor;
    }

    @Override
    protected Runnable decorate(final Runnable command) {
        return tracer.tryPreserve(command);
    }

}
