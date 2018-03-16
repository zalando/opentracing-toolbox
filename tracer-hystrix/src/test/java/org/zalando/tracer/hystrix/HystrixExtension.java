package org.zalando.tracer.hystrix;

import com.netflix.hystrix.Hystrix;
import com.netflix.hystrix.strategy.concurrency.HystrixRequestContext;
import org.junit.jupiter.api.extension.AfterEachCallback;
import org.junit.jupiter.api.extension.BeforeEachCallback;
import org.junit.jupiter.api.extension.ExtensionContext;

final class HystrixExtension implements BeforeEachCallback, AfterEachCallback {

    private HystrixRequestContext context;

    @Override
    public void beforeEach(final ExtensionContext context) {
        this.context = HystrixRequestContext.initializeContext();
        Hystrix.reset();
    }

    @Override
    public void afterEach(final ExtensionContext context) {
        if (this.context != null) {
            this.context.shutdown();
            this.context = null;
        }
    }

}
