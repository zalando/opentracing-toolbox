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

import com.google.common.util.concurrent.MoreExecutors;
import org.zalando.tracer.Tracer;

import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

/**
 * @see Executors
 * @see MoreExecutors
 */
public final class TracingExecutors {

    TracingExecutors() {
        // package private so we can trick code coverage
    }

    public static Executor manage(final Executor executor, final Tracer tracer) {
        return new ManagingExecutor(executor, tracer);
    }

    public static ExecutorService manage(final ExecutorService executor, final Tracer tracer) {
        return new ManagingExecutorService(executor, tracer);
    }

    public static ScheduledExecutorService manage(final ScheduledExecutorService executor, final Tracer tracer) {
        return new ManagingScheduledExecutorService(executor, tracer);
    }

    public static Executor preserve(final Executor executor, final Tracer tracer) {
        return new PreservingExecutor(executor, tracer);
    }

    public static ExecutorService preserve(final ExecutorService executor, final Tracer tracer) {
        return new PreservingExecutorService(executor, tracer);
    }

    public static ScheduledExecutorService preserve(final ScheduledExecutorService executor, final Tracer tracer) {
        return new PreservingScheduledExecutorService(executor, tracer);
    }

}
