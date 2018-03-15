package org.zalando.tracer;

import org.junit.jupiter.api.extension.AfterEachCallback;
import org.junit.jupiter.api.extension.BeforeEachCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.slf4j.MDC;

final class MDCExtension implements BeforeEachCallback, AfterEachCallback {

    @Override
    public void afterEach(final ExtensionContext context) {
        MDC.clear();
    }

    @Override
    public void beforeEach(final ExtensionContext context) {
        MDC.clear();
    }

}
