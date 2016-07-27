package org.zalando.tracer;

/*
 * ⁣​
 * Tracer
 * ⁣⁣
 * Copyright (C) 2015 Zalando SE
 * ⁣⁣
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * ​⁣
 */

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;

import javax.annotation.Nullable;
import java.util.Deque;
import java.util.LinkedList;
import java.util.Optional;
import java.util.Queue;
import java.util.function.BiConsumer;
import java.util.function.Function;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkState;
import static com.google.common.collect.Maps.toMap;

final class StackedTracer implements Tracer {

    private final ImmutableMap<String, ThreadLocal<Deque<String>>> traces;
    private final ImmutableMap<String, Generator> generators;
    private final ImmutableList<TraceListener> listeners;

    StackedTracer(final ImmutableMap<String, Generator> generators,
            final ImmutableList<TraceListener> listeners) {
        this.traces = toMap(generators.keySet(), name -> ThreadLocal.withInitial(LinkedList::new));
        this.generators = generators;
        this.listeners = listeners;
    }

    @Override
    public void start(final Function<String, String> provider) {
        traces.forEach((name, state) -> {
            @Nullable final String present = provider.apply(name);
            final String value = Optional.ofNullable(present)
                    .orElseGet(() -> generators.get(name).generate());

            state.get().add(value);

            listeners.forEach(listener ->
                    listener.onStart(name, value));
        });
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
            final String value = checkValue(name, state.get().removeLast());
            listeners.forEach(listener ->
                    listener.onStop(name, value));
        });
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

    private String checkValue(String name, @Nullable String value) {
        checkState(value != null, "%s has not been started", name);
        return value;
    }

}