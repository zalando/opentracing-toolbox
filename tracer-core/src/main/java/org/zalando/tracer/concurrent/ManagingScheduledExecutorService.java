package org.zalando.tracer.concurrent;

import org.zalando.tracer.Tracer;

import java.util.concurrent.Callable;
import java.util.concurrent.ScheduledExecutorService;

final class ManagingScheduledExecutorService extends DecoratingScheduledExecutorService {

    private final ScheduledExecutorService executor;
    private final Tracer tracer;

    ManagingScheduledExecutorService(final ScheduledExecutorService executor, final Tracer tracer) {
        this.executor = executor;
        this.tracer = tracer;
    }

    @Override
    protected ScheduledExecutorService delegate() {
        return executor;
    }

    @Override
    protected Runnable decorate(final Runnable command) {
        return tracer.manage(command);
    }

    @Override
    protected <T> Callable<T> decorate(final Callable<T> task) {
        return tracer.manage(task);
    }

}
