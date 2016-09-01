package org.zalando.tracer.concurrent;

import javax.annotation.Nonnull;
import java.util.concurrent.Callable;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

abstract class DecoratingScheduledExecutorService extends DecoratingExecutorService implements ScheduledExecutorService {

    @Override
    protected abstract ScheduledExecutorService delegate();

    @Nonnull
    @Override
    public ScheduledFuture<?> scheduleWithFixedDelay(final Runnable command, final long initialDelay, final long delay,
            final TimeUnit unit) {
        return delegate().scheduleWithFixedDelay(decorate(command), initialDelay, delay, unit);
    }

    @Nonnull
    @Override
    public ScheduledFuture<?> scheduleAtFixedRate(final Runnable command, final long initialDelay, final long period,
            final TimeUnit unit) {
        return delegate().scheduleAtFixedRate(decorate(command), initialDelay, period, unit);
    }

    @Nonnull
    @Override
    public <V> ScheduledFuture<V> schedule(final Callable<V> callable, final long delay, final TimeUnit unit) {
        return delegate().schedule(decorate(callable), delay, unit);
    }

    @Nonnull
    @Override
    public ScheduledFuture<?> schedule(final Runnable command, final long delay, final TimeUnit unit) {
        return delegate().schedule(decorate(command), delay, unit);
    }

}