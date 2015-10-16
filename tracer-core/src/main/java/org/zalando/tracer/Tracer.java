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
import lombok.Singular;

import javax.annotation.Nullable;
import java.util.concurrent.Callable;
import java.util.function.BiConsumer;

import static com.google.common.base.MoreObjects.firstNonNull;
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
    void start(Function<String, String> provider);

    /**
     * @throws IllegalStateException
     */
    Trace get(final String name);

    /**
     * @throws IllegalStateException
     */
    void forEach(final BiConsumer<String, String> consumer);

    /**
     * @throws IllegalStateException
     */
    void stop();

    <V> Callable<V> preserve(final Callable<V> callable);

    Runnable preserve(final Runnable runnable);

    static Tracer create(final String... traces) {
        return builder().traces(asList(traces)).build();
    }

    @lombok.Builder(builderClassName = "Builder")
    static Tracer create(@Singular final ImmutableList<String> traces,
            @Nullable final Generator generator,
            @Singular final ImmutableList<TraceListener> listeners) {

        return new DefaultTracer(traces,
                firstNonNull(generator, new UUIDGenerator()),
                listeners);
    }

}
