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
import lombok.Singular;

import java.util.concurrent.Callable;
import java.util.function.BiConsumer;
import java.util.function.Function;

import static com.google.common.collect.Maps.toMap;
import static java.util.Arrays.asList;

public interface Tracer {

    /**
     * @throws IllegalStateException
     */
    default void start() {
        start(trace -> null);
    }

    /**
     * @throws IllegalStateException
     */
    void start(final Function<String, String> provider);

    /**
     * @throws IllegalStateException
     */
    Trace get(final String trace);

    /**
     * @throws IllegalStateException
     */
    void forEach(final BiConsumer<String, String> consumer);
    
    /**
     * @throws IllegalStateException
     */
    default ImmutableMap<String, String> snapshot() {
        final ImmutableMap.Builder<String, String> builder = ImmutableMap.builder();
        forEach(builder::put);
        return builder.build();
    }

    /**
     * @throws IllegalStateException
     */
    void stop();

    default Runnable manage(final Runnable task) {
        return delegate(task, trace -> null);
    }

    /**
     * @throws IllegalStateException
     */
    default Runnable preserve(final Runnable task) {
        return delegate(task, snapshot()::get);
    }

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

    default <V> Callable<V> manage(final Callable<V> task) {
        return delegate(task, trace -> null);
    }

    /**
     * @throws IllegalStateException
     */
    default <V> Callable<V> preserve(final Callable<V> task) {
        return delegate(task, snapshot()::get);
    }

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

    static Tracer create(final String... traces) {
        return builder().traces(asList(traces)).build();
    }

    @lombok.Builder(builderClassName = "Builder")
    static Tracer create(
            @Singular final ImmutableList<String> traces,
            @Singular("trace") final ImmutableMap<String, Generator> customs,
            @Singular final ImmutableList<TraceListener> listeners) {

        final UUIDGenerator defaultGenerator = new UUIDGenerator();
        final ImmutableMap<String, Generator> combined = ImmutableMap.<String, Generator>builder()
                .putAll(customs)
                .putAll(toMap(traces, trace -> defaultGenerator))
                .build();

        return new DefaultTracer(combined, listeners);
    }

}
