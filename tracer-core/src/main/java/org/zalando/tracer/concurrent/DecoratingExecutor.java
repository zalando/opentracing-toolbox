package org.zalando.tracer.concurrent;

import java.util.concurrent.Executor;

abstract class DecoratingExecutor implements Executor {

    protected abstract Executor delegate();

    protected abstract Runnable decorate(final Runnable command);

    @Override
    public void execute(final Runnable command) {
        delegate().execute(decorate(command));
    }

}
