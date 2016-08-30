package org.zalando.tracer;

/**
 * A simplistic bean-style structure that holds the name and the current value of a trace.
 */
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
