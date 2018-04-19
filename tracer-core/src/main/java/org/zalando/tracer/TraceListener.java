package org.zalando.tracer;

import org.apiguardian.api.API;
import org.zalando.tracer.TracerFactory.Builder;

import java.util.function.Function;

import static org.apiguardian.api.API.Status.STABLE;

/**
 * A listener that can be attached to a {@link Tracer tracer} upon creation. It will then observe any lifecycle event
 * of that tracer.
 *
 * <p>
 * Traditional trace listeners are not aware of {@link Builder#stacked() trace stacking}. A stacked trace will be
 * observed as defined by the following call sequence:
 * </p>
 *
 * <ol>
 * <li>{@code onStart("Trace-ID", "A")}</li>
 * <li>{@code onStop("Trace-ID", "A")}</li>
 * <li>{@code onStart("Trace-ID", "B")}</li>
 * <li>{@code onStop("Trace-ID", "B")}</li>
 * <li>{@code onStart("Trace-ID", "A")}</li>
 * <li>{@code onStop("Trace-ID", "A")}</li>
 * </ol>
 *
 * @see Tracer#start()
 * @see Tracer#start(Function)
 * @see Tracer#stop()
 * @see StackedTraceListener
 */
@API(status = STABLE)
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
