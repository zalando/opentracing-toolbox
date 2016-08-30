package org.zalando.tracer.spring;

import org.springframework.core.task.TaskExecutor;
import org.zalando.tracer.Tracer;

final class TracerTaskExecutor implements TaskExecutor {

    private final Tracer tracer;
    private final TaskExecutor delegate;

    TracerTaskExecutor(final Tracer tracer, final TaskExecutor delegate) {
        this.tracer = tracer;
        this.delegate = delegate;
    }

    @Override
    public void execute(final Runnable task) {
        delegate.execute(tracer.preserve(task));
    }

}
