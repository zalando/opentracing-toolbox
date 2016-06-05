package org.zalando.tracer.concurrent;

/*
 * ⁣​
 * Tracer: Core
 * ⁣⁣
 * Copyright (C) 2015 - 2016 Zalando SE
 * ⁣⁣
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * ​⁣
 */

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