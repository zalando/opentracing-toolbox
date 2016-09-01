package org.zalando.tracer;

import com.google.common.collect.ImmutableMap;

import javax.annotation.Nullable;
import java.util.Deque;
import java.util.LinkedList;
import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.Function;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkState;
import static com.google.common.collect.Maps.toMap;

final class StackedTracer implements Tracer {

    private final ImmutableMap<String, ThreadLocal<Deque<String>>> traces;
    private final ImmutableMap<String, Generator> generators;
    private final TraceListener listeners;

    StackedTracer(final ImmutableMap<String, Generator> generators,
            final TraceListener listeners) {
        this.traces = toMap(generators.keySet(), name -> ThreadLocal.withInitial(LinkedList::new));
        this.generators = generators;
        this.listeners = listeners;
    }

    @Override
    public void start(final Function<String, String> provider) {
        traces.forEach((name, state) -> {
            final Deque<String> queue = state.get();
            @Nullable final String previous = queue.peekLast();
            final String current = generate(provider, name);

            queue.add(current);

            runIf(listeners::onStop, name, previous);
            runIf(listeners::onStart, name, current);
        });
    }

    private String generate(final Function<String, String> provider, final String name) {
        return Optional.ofNullable(provider.apply(name))
                .orElseGet(() -> generators.get(name).generate());
    }

    @Override
    public Trace get(final String name) {
        final ThreadLocal<Deque<String>> state = getAndCheckState(name);

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
            final Deque<String> queue = state.get();
            final String previous = checkValue(name, queue.removeLast());
            @Nullable final String current = queue.peekLast();

            runIf(listeners::onStop, name, previous);
            runIf(listeners::onStart, name, current);
        });
    }

    private void runIf(final BiConsumer<String, String> action, final String name, @Nullable final String current) {
        if (current != null) {
            action.accept(name, current);
        }
    }

    private ThreadLocal<Deque<String>> getAndCheckState(final String name) {
        @Nullable final ThreadLocal<Deque<String>> state = traces.get(name);
        checkArgument(state != null, "No such trace: %s", name);
        return state;
    }

    private String getAndCheckValue(final String name, final ThreadLocal<Deque<String>> state) {
        @Nullable final String value = state.get().peekLast();
        return checkValue(name, value);
    }

    private String checkValue(final String name, @Nullable final String value) {
        checkState(value != null, "%s has not been started", name);
        return value;
    }

}