package org.zalando.tracer;

import com.google.common.collect.ImmutableMap;

import javax.annotation.Nullable;
import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.Function;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkState;
import static com.google.common.collect.Maps.toMap;

final class DefaultTracer implements Tracer {

    private final ImmutableMap<String, ThreadLocal<String>> traces;
    private final ImmutableMap<String, Generator> generators;
    private final TraceListener listeners;

    DefaultTracer(final ImmutableMap<String, Generator> generators,
            final TraceListener listeners) {
        this.traces = toMap(generators.keySet(), name -> new ThreadLocal<>());
        this.generators = generators;
        this.listeners = listeners;
    }

    @Override
    public void start(final Function<String, String> provider) {
        traces.forEach((name, state) -> {
            checkState(state.get() == null, "%s is already started", name);

            final String current = generate(provider, name);

            state.set(current);

            listeners.onStart(name, current);
        });
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
        checkArgument(state != null, "No such trace: %s", name);
        return state;
    }

    private String getAndCheckValue(final String name, final ThreadLocal<String> state) {
        @Nullable final String value = state.get();
        checkState(value != null, "%s has not been started", name);
        return value;
    }

}