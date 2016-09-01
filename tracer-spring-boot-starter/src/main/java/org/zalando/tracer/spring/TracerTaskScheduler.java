package org.zalando.tracer.spring;

import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.Trigger;
import org.zalando.tracer.Tracer;

import java.util.Date;
import java.util.concurrent.ScheduledFuture;

final class TracerTaskScheduler implements TaskScheduler {

    private final Tracer tracer;
    private final TaskScheduler delegate;

    TracerTaskScheduler(final Tracer tracer, final TaskScheduler delegate) {
        this.tracer = tracer;
        this.delegate = delegate;
    }

    @Override
    public ScheduledFuture<?> schedule(final Runnable runnable, final Trigger trigger) {
        return delegate.schedule(tracer.manage(runnable), trigger);
    }

    @Override
    public ScheduledFuture<?> schedule(final Runnable runnable, final Date date) {
        return delegate.schedule(tracer.manage(runnable), date);
    }

    @Override
    public ScheduledFuture<?> scheduleAtFixedRate(final Runnable runnable, final Date date, final long period) {
        return delegate.scheduleAtFixedRate(tracer.manage(runnable), date, period);
    }

    @Override
    public ScheduledFuture<?> scheduleAtFixedRate(final Runnable runnable, final long period) {
        return delegate.scheduleAtFixedRate(tracer.manage(runnable), period);
    }

    @Override
    public ScheduledFuture<?> scheduleWithFixedDelay(final Runnable runnable, final Date date, final long delay) {
        return delegate.scheduleWithFixedDelay(tracer.manage(runnable), date, delay);
    }

    @Override
    public ScheduledFuture<?> scheduleWithFixedDelay(final Runnable runnable, final long delay) {
        return delegate.scheduleWithFixedDelay(tracer.manage(runnable), delay);
    }

}
