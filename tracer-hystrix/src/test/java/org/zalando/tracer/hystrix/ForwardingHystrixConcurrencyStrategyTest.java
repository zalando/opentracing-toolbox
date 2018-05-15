package org.zalando.tracer.hystrix;

import com.netflix.hystrix.HystrixThreadPoolKey;
import com.netflix.hystrix.strategy.concurrency.HystrixConcurrencyStrategy;
import com.netflix.hystrix.strategy.properties.HystrixProperty;
import org.junit.jupiter.api.Test;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

import static com.netflix.hystrix.HystrixThreadPoolKey.Factory.asKey;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

class ForwardingHystrixConcurrencyStrategyTest {

    @Test
    void getThreadPool() {
        final HystrixConcurrencyStrategy delegate = mock(HystrixConcurrencyStrategy.class);

        final HystrixConcurrencyStrategy unit = new ForwardingHystrixConcurrencyStrategy() {
            @Override
            protected HystrixConcurrencyStrategy delegate() {
                return delegate;
            }
        };

        final HystrixThreadPoolKey threadPoolKey = asKey("key");
        final HystrixProperty<Integer> property = HystrixProperty.Factory.asProperty(1);
        final BlockingQueue<Runnable> queue = new ArrayBlockingQueue<>(1);
        unit.getThreadPool(threadPoolKey, property, property, property, SECONDS, queue);

        verify(delegate).getThreadPool(threadPoolKey, property, property, property, SECONDS, queue);
    }

}
