package org.zalando.tracer;

import org.junit.rules.TestWatcher;
import org.junit.runner.Description;
import org.slf4j.MDC;

public final class MDCRule extends TestWatcher {

    @Override
    protected void starting(final Description description) {
        MDC.clear();
    }

    @Override
    protected void finished(final Description description) {
        MDC.clear();
    }

}
