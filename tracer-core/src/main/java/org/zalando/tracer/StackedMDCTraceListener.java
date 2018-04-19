package org.zalando.tracer;

import org.apiguardian.api.API;

import javax.annotation.Nullable;

import static org.apiguardian.api.API.Status.STABLE;
import static org.slf4j.MDC.get;
import static org.slf4j.MDC.put;

@API(status = STABLE)
public final class StackedMDCTraceListener implements StackedTraceListener {

    @Override
    public void onStart(final String name, final String value) {
        put(name, push(get(name), value));
    }

    private String push(@Nullable final String previous, final String value) {
        return previous == null ? value : previous + " " + value;
    }

    @Override
    public void onStop(final String name, final String value) {
        put(name, pop(get(name), value));
    }

    @Nullable
    private String pop(final String previous, final String value) {
        return value.equals(previous) ? null :  previous.substring(0, previous.lastIndexOf(' '));
    }

}
