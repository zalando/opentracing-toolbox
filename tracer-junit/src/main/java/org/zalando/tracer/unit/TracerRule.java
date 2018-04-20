package org.zalando.tracer.unit;

import org.apiguardian.api.API;
import org.junit.rules.TestWatcher;
import org.junit.runner.Description;
import org.zalando.tracer.Tracer;

import javax.inject.Inject;

import static org.apiguardian.api.API.Status.STABLE;

@API(status = STABLE)
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
