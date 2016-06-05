package org.zalando.tracer.hystrix;

/*
 * ⁣​
 * Tracer: Hystrix
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

import com.netflix.hystrix.HystrixThreadPoolKey;
import com.netflix.hystrix.strategy.concurrency.HystrixConcurrencyStrategy;
import com.netflix.hystrix.strategy.concurrency.HystrixRequestVariable;
import com.netflix.hystrix.strategy.concurrency.HystrixRequestVariableLifecycle;
import com.netflix.hystrix.strategy.properties.HystrixProperty;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Callable;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

abstract class ForwardingHystrixConcurrencyStrategy extends HystrixConcurrencyStrategy {

    protected abstract HystrixConcurrencyStrategy delegate();

    @Override
    public BlockingQueue<Runnable> getBlockingQueue(final int maxQueueSize) {
        return delegate().getBlockingQueue(maxQueueSize);
    }

    @Override
    public <T> HystrixRequestVariable<T> getRequestVariable(final HystrixRequestVariableLifecycle<T> rv) {
        return delegate().getRequestVariable(rv);
    }

    @Override
    public ThreadPoolExecutor getThreadPool(final HystrixThreadPoolKey threadPoolKey, final HystrixProperty<Integer> corePoolSize, final HystrixProperty<Integer> maximumPoolSize, final HystrixProperty<Integer> keepAliveTime, final TimeUnit unit, final BlockingQueue<Runnable> workQueue) {
        return delegate().getThreadPool(threadPoolKey, corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue);
    }

    @Override
    public <T> Callable<T> wrapCallable(final Callable<T> callable) {
        return delegate().wrapCallable(callable);
    }

}
