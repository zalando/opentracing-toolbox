package org.zalando.tracer;

/*
 * ⁣​
 * Tracer: Core
 * ⁣⁣
 * Copyright (C) 2015 - 2016 Zalando SE
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

import static com.google.common.collect.Maps.toMap;

public final class TracerCreator {

    TracerCreator() {
        // package private so we can trick code coverage
    }

    @lombok.Builder(builderClassName = "Builder")
    static Tracer create(
            final boolean stacked,
            @Singular final ImmutableList<String> traces,
            @Singular("trace") final ImmutableMap<String, Generator> customs,
            @Singular final ImmutableList<TraceListener> listeners) {

        final UUIDGenerator defaultGenerator = new UUIDGenerator();
        final ImmutableMap<String, Generator> combined = ImmutableMap.<String, Generator>builder()
                .putAll(customs)
                .putAll(toMap(traces, trace -> defaultGenerator))
                .build();

        if (stacked) {
            return new StackedTracer(combined, listeners);
        } else {
            return new DefaultTracer(combined, listeners);
        }
    }

}
