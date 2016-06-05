package org.zalando.tracer.concurrent;

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

import com.google.common.util.concurrent.SettableFuture;
import org.junit.Test;
import org.zalando.tracer.Trace;
import org.zalando.tracer.Tracer;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.junit.Assert.assertThat;

public final class ManagingExecutorTest {

    private final Executor executor = Executors.newSingleThreadExecutor();
    private final Tracer tracer = Tracer.create("X-Trace");
    private final Executor unit = TracingExecutors.manage(executor, tracer);

    @Test
    public void shouldNotRequireActiveTrace() {
        unit.execute(() -> tracer.get("X-Trace").getValue());
    }

    @Test
    public void shouldManageTrace() throws InterruptedException, ExecutionException, TimeoutException {
        final SettableFuture<String> future = SettableFuture.create();
        final Trace trace = tracer.get("X-Trace");

        tracer.start();
        final String expected = trace.getValue();
        try {
            unit.execute(() -> future.set(trace.getValue()));
        } finally {
            tracer.stop();
        }

        assertThat(future.get(100, TimeUnit.MILLISECONDS), is(not(expected)));
    }

}