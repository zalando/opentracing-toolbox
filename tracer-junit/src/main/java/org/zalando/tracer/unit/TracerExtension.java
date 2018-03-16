package org.zalando.tracer.unit;

import org.junit.jupiter.api.extension.AfterEachCallback;
import org.junit.jupiter.api.extension.BeforeEachCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.zalando.tracer.Tracer;

import javax.inject.Inject;

public final class TracerExtension implements BeforeEachCallback, AfterEachCallback {

    private final Tracer tracer;

    @Inject
    public TracerExtension(final Tracer tracer) {
        this.tracer = tracer;
    }

    @Override
    public void beforeEach(final ExtensionContext context) {
        tracer.start();
    }

    @Override
    public void afterEach(final ExtensionContext context) {
        tracer.stop();
    }

}
