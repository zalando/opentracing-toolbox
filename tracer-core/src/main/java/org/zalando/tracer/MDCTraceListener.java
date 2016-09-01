package org.zalando.tracer;

import org.slf4j.MDC;

public final class MDCTraceListener implements TraceListener {

    @Override
    public void onStart(final String name, final String value) {
        MDC.put(name, value);
    }

    @Override
    public void onStop(final String name, final String value) {
        MDC.remove(name);
    }

}
