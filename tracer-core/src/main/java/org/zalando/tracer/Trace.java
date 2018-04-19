package org.zalando.tracer;

import org.apiguardian.api.API;

import static org.apiguardian.api.API.Status.STABLE;

/**
 * A simplistic bean-style structure that holds the name and the current value of a trace.
 */
@API(status = STABLE)
public interface Trace {

    /**
     * Provides this trace's name.
     *
     * @return the name of this trace
     */
    String getName();

    /**
     * Provides the current value of this trace.
     *
     * @return the current value of this trace
     * @throws IllegalStateException if this trace is not active
     */
    String getValue();

}
