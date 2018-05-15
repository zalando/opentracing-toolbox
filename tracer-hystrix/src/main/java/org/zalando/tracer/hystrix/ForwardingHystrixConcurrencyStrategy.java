package org.zalando.tracer.hystrix;

import com.netflix.hystrix.HystrixThreadPoolKey;
import com.netflix.hystrix.HystrixThreadPoolProperties;
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
    public ThreadPoolExecutor getThreadPool(final HystrixThreadPoolKey threadPoolKey, final HystrixProperty<Integer> corePoolSize, final HystrixProperty<Integer> maximumPoolSize, final HystrixProperty<Integer> keepAliveTime, final TimeUnit unit, final BlockingQueue<Runnable> workQueue) {
        return delegate().getThreadPool(threadPoolKey, corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue);
    }

    // omitted @Override to be compatible with older hystrix version
    public ThreadPoolExecutor getThreadPool(final HystrixThreadPoolKey threadPoolKey,
            final HystrixThreadPoolProperties threadPoolProperties) {
        return super.getThreadPool(threadPoolKey, threadPoolProperties);
    }

    @Override
    public BlockingQueue<Runnable> getBlockingQueue(final int maxQueueSize) {
        return delegate().getBlockingQueue(maxQueueSize);
    }

    @Override
    public <T> Callable<T> wrapCallable(final Callable<T> callable) {
        return delegate().wrapCallable(callable);
    }

    @Override
    public <T> HystrixRequestVariable<T> getRequestVariable(final HystrixRequestVariableLifecycle<T> rv) {
        return delegate().getRequestVariable(rv);
    }

}
