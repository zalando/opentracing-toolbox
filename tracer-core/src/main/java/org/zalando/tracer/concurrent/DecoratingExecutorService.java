package org.zalando.tracer.concurrent;

import javax.annotation.Nonnull;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import static java.util.stream.Collectors.toList;

abstract class DecoratingExecutorService extends DecoratingExecutor implements ExecutorService {

    @Override
    protected abstract ExecutorService delegate();

    protected abstract <T> Callable<T> decorate(final Callable<T> task);

    private <T> List<Callable<T>> decorate(final Collection<? extends Callable<T>> tasks) {
        return tasks.stream().map(this::decorate).collect(toList());
    }

    @Override
    public <T> T invokeAny(final Collection<? extends Callable<T>> tasks, final long timeout, final TimeUnit unit)
            throws InterruptedException, ExecutionException, TimeoutException {
        return delegate().invokeAny(decorate(tasks), timeout, unit);
    }

    @Nonnull
    @Override
    public <T> T invokeAny(final Collection<? extends Callable<T>> tasks)
            throws InterruptedException, ExecutionException {
        return delegate().invokeAny(decorate(tasks));
    }

    @Nonnull
    @Override
    public <T> List<Future<T>> invokeAll(final Collection<? extends Callable<T>> tasks, final long timeout,
            final TimeUnit unit) throws InterruptedException {
        return delegate().invokeAll(decorate(tasks), timeout, unit);
    }

    @Nonnull
    @Override
    public <T> List<Future<T>> invokeAll(final Collection<? extends Callable<T>> tasks) throws InterruptedException {
        return delegate().invokeAll(decorate(tasks));
    }

    @Nonnull
    @Override
    public Future<?> submit(final Runnable task) {
        return delegate().submit(decorate(task));
    }

    @Nonnull
    @Override
    public <T> Future<T> submit(final Runnable task, final T result) {
        return delegate().submit(decorate(task), result);
    }

    @Nonnull
    @Override
    public <T> Future<T> submit(final Callable<T> task) {
        return delegate().submit(decorate(task));
    }

    @Override
    public boolean awaitTermination(final long timeout, final TimeUnit unit) throws InterruptedException {
        return delegate().awaitTermination(timeout, unit);
    }

    @Override
    public boolean isTerminated() {
        return delegate().isTerminated();
    }

    @Override
    public boolean isShutdown() {
        return delegate().isShutdown();
    }

    @Nonnull
    @Override
    public List<Runnable> shutdownNow() {
        return delegate().shutdownNow();
    }

    @Override
    public void shutdown() {
        delegate().shutdown();
    }
}
