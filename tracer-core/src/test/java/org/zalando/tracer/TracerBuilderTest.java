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

import com.google.common.collect.ImmutableMap;
import org.junit.After;
import org.junit.Test;

import static java.util.Collections.singleton;
import static java.util.Collections.singletonMap;
import static org.mockito.Mockito.mock;

public final class TracerBuilderTest {

    private final TracerFactory.Builder unit = Tracer.builder();

    @After
    public void verifyThatTracerCanBeBuild() {
        unit.build();
    }

    @Test
    public void shouldHandleMultipleTrace() {
        unit.trace("X-Trace-ID");
        unit.trace("X-Request-ID");
    }

    @Test
    public void shouldHandleMultipleTraces() {
        unit.traces(singleton("X-Trace-ID"));
        unit.traces(singleton("X-Request-ID"));
    }

    @Test
    public void shouldHandleMultipleTraceWithGenerator() {
        unit.trace("X-Trace-ID", new UUIDGenerator());
        unit.trace("X-Request-ID", new UUIDGenerator());
    }

    @Test
    public void shouldHandleMultipleTracesWithGenerator() {
        unit.customs(singletonMap("X-Trace-ID", new UUIDGenerator()));
        unit.customs(singletonMap("X-Request-ID", new UUIDGenerator()));
    }

    @Test
    public void shouldHandleMultipleListener() {
        unit.listener(new LoggingTraceListener());
        unit.listener(new MDCTraceListener());
    }

    @Test
    public void shouldHandleMultipleListeners() {
        unit.listeners(singleton(new LoggingTraceListener()));
        unit.listeners(singleton(new MDCTraceListener()));
    }

    @Test
    public void shouldClearTraces() {
        unit.clearTraces();
        unit.trace("X-Foo-ID");
        unit.clearTraces();
    }

    @Test
    public void shouldClearCustoms() {
        unit.clearCustoms();
        unit.customs(ImmutableMap.of("X-Foo-ID", () -> "foo"));
        unit.clearCustoms();
    }

    @Test
    public void shouldClearListeners() {
        unit.clearListeners();
        unit.listener(mock(TraceListener.class));
        unit.clearListeners();
    }

}
