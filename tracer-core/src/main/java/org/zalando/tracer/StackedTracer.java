package org.zalando.tracer;

import javax.annotation.Nullable;
import java.util.Collection;
import java.util.Deque;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.Function;

import static java.util.function.Function.identity;
import static java.util.stream.Collectors.partitioningBy;
import static java.util.stream.Collectors.toMap;
import static org.zalando.tracer.TraceListeners.compound;

final class StackedTracer implements Tracer {

    private final Map<String, ThreadLocal<Deque<String>>> traces;
    private final Map<String, Generator> generators;
    private final TraceListener listeners;
    private final TraceListener stackedListeners;

    StackedTracer(final Map<String, Generator> generators,
            final Collection<TraceListener> listeners) {
        this.traces = generators.keySet().stream()
                .collect(toMap(identity(), name -> ThreadLocal.withInitial(LinkedList::new)));
        this.generators = generators;

        final Map<Boolean, List<TraceListener>> partitions = listeners.stream()
                .collect(partitioningBy(StackedTraceListener.class::isInstance));

        this.listeners = compound(partitions.get(Boolean.FALSE));
        this.stackedListeners = compound(partitions.get(Boolean.TRUE));
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
            runIf(stackedListeners::onStart, name, current);
        });
    }

    @Override
    public boolean isActive() {
        return traces.values().stream()
                .map(ThreadLocal::get)
                .anyMatch(queue -> !queue.isEmpty());
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
            @Nullable final String previous = checkValue(name, queue.pollLast());
            @Nullable final String current = queue.peekLast();

            runIf(listeners::onStop, name, previous);
            runIf(stackedListeners::onStop, name, previous);
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

        if (state == null) {
            throw new IllegalArgumentException("No such trace: " + name);
        }
        return state;
    }

    private String getAndCheckValue(final String name, final ThreadLocal<Deque<String>> state) {
        @Nullable final String value = state.get().peekLast();
        return checkValue(name, value);
    }

    private String checkValue(final String name, @Nullable final String value) {
        if (value == null) {
            throw new IllegalStateException(name + " has not been started");
        }

        return value;
    }

}
