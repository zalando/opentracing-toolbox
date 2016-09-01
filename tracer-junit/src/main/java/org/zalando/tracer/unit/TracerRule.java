package org.zalando.tracer.unit;

import org.junit.rules.TestWatcher;
import org.junit.runner.Description;
import org.zalando.tracer.Tracer;

import javax.inject.Inject;

public final class TracerRule extends TestWatcher {

    private final Tracer tracer;

    @Inject
    public TracerRule(final Tracer tracer) {
        this.tracer = tracer;
    }

    @Override
    protected void starting(final Description description) {
        tracer.start();
    }

    @Override
    protected void finished(final Description description) {
        tracer.stop();
    }

}
