package org.zalando.tracer.concurrent;

import com.google.common.collect.ForwardingObject;

import java.util.concurrent.Executor;

abstract class DecoratingExecutor extends ForwardingObject implements Executor {

    @Override
    protected abstract Executor delegate();

    protected abstract Runnable decorate(final Runnable command);

    @Override
    public void execute(final Runnable command) {
        delegate().execute(decorate(command));
    }

}
