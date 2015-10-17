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

import com.google.common.base.Function;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;

import javax.annotation.Nullable;
import java.util.Optional;
import java.util.function.BiConsumer;

import static com.google.common.base.Preconditions.checkState;
import static com.google.common.collect.Maps.toMap;

final class DefaultTracer implements Tracer {

    private final ImmutableMap<String, ThreadLocal<String>> traces;
    private final ImmutableMap<String, Generator> generators;
    private final ImmutableList<TraceListener> listeners;

    DefaultTracer(final ImmutableMap<String, Generator> generators,
            final ImmutableList<TraceListener> listeners) {
        this.traces = toMap(generators.keySet(), trace -> new ThreadLocal<>());
        this.generators = generators;
        this.listeners = listeners;
    }

    @Override
    public void start(final Function<String, String> provider) {
        traces.forEach((trace, state) -> {
            checkState(state.get() == null, "%s is already started", trace);

            @Nullable final String present = provider.apply(trace);
            final String value = Optional.ofNullable(present)
                    .orElseGet(() -> generators.get(trace).generate());

            state.set(value);

            listeners.forEach(listener ->
                    listener.onStart(trace, value));
        });
    }

    @Override
    public Trace get(final String trace) {
        final ThreadLocal<String> state = getAndCheckState(trace);

        return () ->
                getAndCheckValue(trace, state);
    }

    @Override
    public void forEach(final BiConsumer<String, String> consumer) {
        traces.forEach((trace, state) ->
                consumer.accept(trace, getAndCheckValue(trace, state)));
    }

    @Override
    public void stop() {
        traces.forEach((trace, state) -> {
            final String value = getAndCheckValue(trace, state);
            state.remove();
            listeners.forEach(listener ->
                    listener.onStop(trace, value));
        });
    }

    private ThreadLocal<String> getAndCheckState(final String name) {
        @Nullable final ThreadLocal<String> state = traces.get(name);
        checkState(state != null, "No such trace: %s", name);
        return state;
    }

    private String getAndCheckValue(final String trace, final ThreadLocal<String> state) {
        @Nullable final String value = state.get();
        checkState(value != null, "%s has not been started", trace);
        return value;
    }

}