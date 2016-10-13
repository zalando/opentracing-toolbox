package org.zalando.tracer;

import javax.annotation.Nullable;
import java.util.Collection;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.Function;

import static java.util.function.Function.identity;
import static java.util.stream.Collectors.toMap;
import static org.zalando.tracer.TraceListeners.compound;

final class DefaultTracer implements Tracer {

    private final Map<String, ThreadLocal<String>> traces;
    private final Map<String, Generator> generators;
    private final TraceListener listeners;

    DefaultTracer(final Map<String, Generator> generators,
            final Collection<TraceListener> listeners) {
        this.traces = generators.keySet().stream()
                .collect(toMap(identity(), name -> new ThreadLocal<>()));
        this.generators = generators;
        this.listeners = compound(listeners);
    }

    @Override
    public void start(final Function<String, String> provider) {
        traces.forEach((name, state) -> {
            if (state.get() != null) {
                throw new IllegalStateException(name + " is already started");
            }

            final String current = generate(provider, name);

            state.set(current);

            listeners.onStart(name, current);
        });
    }

    @Override
    public boolean isActive() {
        return traces.values().stream()
                .map(ThreadLocal::get)
                .anyMatch(Objects::nonNull);
    }

    private String generate(final Function<String, String> provider, final String name) {
        return Optional.ofNullable(provider.apply(name))
                .orElseGet(() -> generators.get(name).generate());
    }

    @Override
    public Trace get(final String name) {
        final ThreadLocal<String> state = getAndCheckState(name);

        return new Trace() {
            @Override
            public String getName() {
                return name;
            }

            @Override
            public String getValue() {
                return getAndCheckValue(name, state);
            }
        };
    }

    @Override
    public void forEach(final BiConsumer<String, String> consumer) {
        traces.forEach((name, state) ->
                consumer.accept(name, getAndCheckValue(name, state)));
    }

    @Override
    public void stop() {
        traces.forEach((name, state) -> {
            final String previous = getAndCheckValue(name, state);
            state.remove();
            listeners.onStop(name, previous);
        });
    }

    private ThreadLocal<String> getAndCheckState(final String name) {
        @Nullable final ThreadLocal<String> state = traces.get(name);

        if (state == null) {
            throw new IllegalArgumentException("No such trace: " + name);
        }

        return state;
    }

    private String getAndCheckValue(final String name, final ThreadLocal<String> state) {
        @Nullable final String value = state.get();

        if (value == null) {
            throw new IllegalStateException(name + " has not been started");
        }

        return value;
    }

}