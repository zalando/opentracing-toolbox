package org.zalando.tracer;

import org.zalando.tracer.TracerFactory.Builder;

/**
 * {@link StackedTraceListener} are a special kind of {@link TraceListener}, because they are aware of
 * {@link Builder#stacked() trace stacking}. A stacked trace will, in contrast to {@link TraceListener}, be observed
 * as defined by the following call sequence:
 *
 * <ol>
 * <li>{@code onStart("Trace-ID", "A")}</li>
 * <li>{@code onStart("Trace-ID", "B")}</li>
 * <li>{@code onStop("Trace-ID", "B")}</li>
 * <li>{@code onStop("Trace-ID", "A")}</li>
 * </ol>
 *
 *
 * @see TraceListener
 * @see Builder#stacked()
 */
public interface StackedTraceListener extends TraceListener {

}
