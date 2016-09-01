package org.zalando.tracer;

import java.util.function.Function;

/**
 * A listener that can be attached to a {@link Tracer tracer} upon creation. It will then observe any lifecycle event
 * of that tracer.
 *
 * @see Tracer#start()
 * @see Tracer#start(Function)
 * @see Tracer#stop()
 */
public interface TraceListener {

    /**
     * Callback to be triggered after {@link Tracer#start() start}.
     *
     * @param name the trace's name
     * @param value the trace's value after start
     */
    void onStart(final String name, final String value);

    /**
     * Callback to be triggered before {@link Tracer#start() stop}.
     *
     * @param name the trace's name
     * @param value the trace's value before stop
     */
    void onStop(final String name, final String value);

}
