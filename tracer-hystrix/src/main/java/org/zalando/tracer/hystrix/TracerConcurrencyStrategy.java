package org.zalando.tracer.hystrix;

import com.netflix.hystrix.strategy.concurrency.HystrixConcurrencyStrategy;
import org.zalando.tracer.Tracer;

import java.util.concurrent.Callable;

public final class TracerConcurrencyStrategy extends ForwardingHystrixConcurrencyStrategy {

    private final Tracer tracer;
    private final HystrixConcurrencyStrategy delegate;

    public TracerConcurrencyStrategy(final Tracer tracer, final HystrixConcurrencyStrategy delegate) {
        this.tracer = tracer;
        this.delegate = delegate;
    }

    @Override
    protected HystrixConcurrencyStrategy delegate() {
        return delegate;
    }

    @Override
    public <T> Callable<T> wrapCallable(final Callable<T> callable) {
        return tracer.preserve(super.wrapCallable(callable));
    }

}
