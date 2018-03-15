package org.zalando.tracer.concurrent;

import org.zalando.tracer.Tracer;

import java.util.concurrent.ScheduledExecutorService;

final class PreservingScheduledExecutorServiceTest extends AbstractPreservingScheduledExecutorServiceTest {

    @Override
    protected ScheduledExecutorService unit(final ScheduledExecutorService executor, final Tracer tracer) {
        return TracingExecutors.preserve(executor, tracer);
    }

}
