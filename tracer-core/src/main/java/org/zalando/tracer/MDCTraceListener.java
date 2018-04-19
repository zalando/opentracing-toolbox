package org.zalando.tracer;

import org.apiguardian.api.API;

import static org.apiguardian.api.API.Status.STABLE;
import static org.slf4j.MDC.put;
import static org.slf4j.MDC.remove;

@API(status = STABLE)
public final class MDCTraceListener implements TraceListener {

    @Override
    public void onStart(final String name, final String value) {
        put(name, value);
    }

    @Override
    public void onStop(final String name, final String value) {
        remove(name);
    }

}
