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

/**
 * A simplistic bean-style structure that holds the name and the current value of a trace.
 */
public interface Trace {

    /**
     * Provides this trace's name.
     *
     * @return the name of this trace
     */
    String getName();

    /**
     * Provides the current value of this trace.
     *
     * @return the current value of this trace
     * @throws IllegalStateException if this trace is not active
     */
    String getValue();

}
