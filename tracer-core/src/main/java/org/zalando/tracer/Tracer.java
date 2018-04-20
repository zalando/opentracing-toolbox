package org.zalando.tracer;

import org.apiguardian.api.API;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.function.BiConsumer;
import java.util.function.Function;

import static java.util.Arrays.asList;
import static org.apiguardian.api.API.Status.STABLE;

/**
 * A tracer is a lifecycle manager of one or more {@link Trace traces}. Traces are identified by name, e.g.
 * {@code X-Trace-ID} and, during an active lifecycle, they can have a value, e.g.
 * {@code 5dc90f18-231f-11e6-a17d-0f84f5d57f60}. Values can either be provided on a per-lifecycle basis or generated
 * on-demand by a {@link Generator generator}. Lifecycle events, i.e. start and stop, can be observed by registering
 * one or more {@link TraceListener listeners}.
 *
 * @see Trace
 * @see Generator
 * @see TraceListener
 */
@API(status = STABLE)
public interface Tracer {

    /**
     * Starts a new trace lifecycle without transporting any trace identifiers.
     * <p>
     * The most common use case would be to manage a trace lifecycle in a scheduled background job or during tests.
     * </p>
     * <pre>
     * tracer.start();
     *
     * try {
     *     // do some background work here
     * } finally {
     *     tracer.stop();
     * }
     * </pre>
     *
     * @throws IllegalStateException if this tracer is already started
     */
    default void start() {
        start(name -> null);
    }

    /**
     * Starts a new trace lifecycle with the possibility to transport given trace identifiers.
     * <p>
     * The most common use case would be to read headers from an incoming HTTP request and hand them over to this
     * tracer in order to persist them:
     * </p>
     * <pre>
     * tracer.start(request::getHeader)
     *
     * try {
     *     // continue with processing the request
     * } finally {
     *     tracer.stop();
     * }
     * </pre>
     * <p>
     * If the provider returns null a value will be created using the configured {@link Generator generator}.
     * </p>
     *
     * @param provider a provider for trace identifiers, will be called with individual trace names
     * @throws IllegalStateException if this tracer is already started
     */
    void start(final Function<String, String> provider);

    boolean isActive();

    /**
     * Retrieves the {@link Trace trace} with the given name. The returned instance is a thread-safe live-view that
     * allows to observe future changes to that trace's lifecycle.
     * <p>
     * This can be used to manually access a trace value, e.g. for additional audit logging:
     * </p>
     * <pre>
     * entity.setModifiedBy(trace.getValue());
     * db.persist(entity);
     * </pre>
     *
     * @param name the name of the trace
     * @return the trace
     * @throws IllegalArgumentException if no trace was configured for the given name
     */
    Trace get(final String name);

    /**
     * Iterates all configured traces and allows to observe their current values.
     * <p>
     * The most common use case woule be to pass all traces onto another system by adding them to an outgoing
     * HTTP request:
     * </p>
     * <pre>
     * tracer.forEach(request::addHeader);
     * </pre>
     *
     * @param consumer a consumer for traces, will be called with trace name-value pairs
     * @throws IllegalStateException if this tracer not started
     */
    void forEach(final BiConsumer<String, String> consumer);

    /**
     * Provides an immutable snapshot of all configured traces mapped by name to their current values.
     *
     * @return an immutable snapshot of all traces
     * @throws IllegalStateException if this tracer not started
     */
    default Map<String, String> snapshot() {
        final Map<String, String> map = new LinkedHashMap<>();
        forEach(map::put);
        return Collections.unmodifiableMap(map);
    }

    /**
     * Stops the current lifecycle.
     *
     * @throws IllegalStateException if this tracer is not started
     */
    void stop();

    /**
     * Creates a {@link Runnable runnable}, based on the given original, that will manage a completely new lifecycle
     * for every invocation,
     *
     * @param task the original runnable
     * @return a runnable that will add lifecycle management to the given runnable
     * @see #delegate(Runnable, Function)
     */
    default Runnable manage(final Runnable task) {
        return delegate(task, name -> null);
    }

    /**
     * Creates a {@link Runnable runnable}, based on the given original, that will preserve the current state of
     * all traces for every invocation.
     *
     * @param task the original runnable
     * @return a runnable that will add lifecycle management to the given runnable
     * @throws IllegalStateException if this tracer is not started
     * @see #snapshot()
     * @see #delegate(Runnable, Function)
     */
    default Runnable preserve(final Runnable task) {
        return delegate(task, snapshot()::get);
    }

    /**
     * Creates a {@link Runnable runnable}, based on the given original, that will preserve the current state of
     * all traces for every invocation, if a trace is currently active. Otherwise it will manage a completely new
     * lifecycle for every invocation.
     *
     * @param task the original runnable
     * @return a runnable that will add lifecycle management to the given runnable
     * @see #snapshot()
     * @see #preserve(Runnable)
     * @see #manage(Runnable)
     */
    default Runnable tryPreserve(final Runnable task) {
        return isActive() ? preserve(task) : manage(task);
    }

    /**
     * Creates a delegating {@link Runnable runnable}, based on the given original, that will manage the tracer
     * lifecycle for every invocation. Initial trace values can be seeded with the given provider.
     *
     * @param task     the original runnable
     * @param provider a provider for trace identifiers, will be called with individual trace names
     * @return a runnable that will add lifecycle management to the given runnable
     * @see #start(Function)
     * @see #manage(Runnable)
     * @see #preserve(Runnable)
     */
    default Runnable delegate(final Runnable task, final Function<String, String> provider) {
        return () -> {
            start(provider);

            try {
                task.run();
            } finally {
                stop();
            }
        };
    }

    /**
     * Creates a {@link Callable callable}, based on the given original, that will manage a completely new lifecycle
     * for every invocation,
     *
     * @param <V>  generic task return value
     * @param task the original callable
     * @return a callable that will add lifecycle management to the given callable
     * @see #delegate(Callable, Function)
     */
    default <V> Callable<V> manage(final Callable<V> task) {
        return delegate(task, name -> null);
    }

    /**
     * Creates a {@link Callable callable}, based on the given original, that will preserve the current state of
     * all traces for every invocation.
     *
     * @param <V>  generic task return value
     * @param task the original callable
     * @return a callable that will add lifecycle management to the given callable
     * @throws IllegalStateException if this tracer is not started
     * @see #snapshot()
     * @see #delegate(Callable, Function)
     */
    default <V> Callable<V> preserve(final Callable<V> task) {
        return delegate(task, snapshot()::get);
    }

    /**
     * Creates a {@link Callable callable}, based on the given original, that will preserve the current state of
     * all traces for every invocation, if a trace is currently active. Otherwise it will manage a completely new
     * lifecycle for every invocation.
     *
     * @param <V>  generic task return value
     * @param task the original callable
     * @return a callable that will add lifecycle management to the given callable
     * @see #snapshot()
     * @see #preserve(Callable)
     * @see #manage(Callable)
     */
    default <V> Callable<V> tryPreserve(final Callable<V> task) {
        return isActive() ? preserve(task) : manage(task);
    }

    /**
     * Creates a delegating {@link Callable callable}, based on the given original, that will manage the tracer
     * lifecycle for every invocation. Initial trace values can be seeded with the given provider.
     *
     * @param <V>      generic task return value
     * @param task     the original callable
     * @param provider a provider for trace identifiers, will be called with individual trace names
     * @return a callable that will add lifecycle management to the given callable
     * @see #start(Function)
     * @see #manage(Callable)
     * @see #preserve(Callable)
     */
    default <V> Callable<V> delegate(final Callable<V> task, final Function<String, String> provider) {
        return () -> {
            start(provider);

            try {
                return task.call();
            } finally {
                stop();
            }
        };
    }

    /**
     * Creates a new tracer that will manage the given traces. All traces will be using the {@link UUIDGenerator} by
     * default. The returned instance will not have any {@link TraceListener listeners} attached to it.
     *
     * @param names trace names to be configured
     * @return a new tracer
     */
    static Tracer create(final String... names) {
        return builder().traces(asList(names)).build();
    }

    static TracerFactory.Builder builder() {
        return TracerFactory.builder();
    }

}
