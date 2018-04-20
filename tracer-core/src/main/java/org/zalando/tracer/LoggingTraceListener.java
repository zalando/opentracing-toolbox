package org.zalando.tracer;

import org.apiguardian.api.API;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.apiguardian.api.API.Status.INTERNAL;
import static org.apiguardian.api.API.Status.STABLE;

@API(status = STABLE)
public final class LoggingTraceListener implements TraceListener {

    private final Logger logger;

    public LoggingTraceListener() {
        this(LoggerFactory.getLogger(Tracer.class));
    }

    public LoggingTraceListener(final Logger logger) {
        this.logger = logger;
    }

    @API(status = INTERNAL)
    public Logger getLogger() {
        return logger;
    }

    @Override
    public void onStart(final String name, final String value) {
        logger.trace("Started [{}] trace: [{}]", name, value);
    }

    @Override
    public void onStop(final String name, final String value) {
        logger.trace("Stopped [{}] trace: [{}]", name, value);
    }

}
