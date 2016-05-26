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

import java.util.function.Function;

/**
 * A listener that can be attached to a {@link Tracer tracer} upon creation. It will then observe any lifecycle event
 * of that tracer.
 *
 * @see Tracer#start()
 * @see Tracer#start(Function)
 * @see Tracer#stop()
 */
public interface TraceListener {

    /**
     * Callback to be triggered after {@link Tracer#start() start}.
     *
     * @param name the trace's name
     * @param value the trace's value after start
     */
    void onStart(final String name, final String value);

    /**
     * Callback to be triggered before {@link Tracer#start() stop}.
     *
     * @param name the trace's name
     * @param value the trace's value before stop
     */
    void onStop(final String name, final String value);

}
