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

import java.util.function.Function;

/**
 * A generator is used to create a new trace identifier when a new lifecycle begins and no existing identifier exist.
 * This usually happens for incoming requests from external systems that are not aware of the special trace header or
 * if someone explicitly decides to start a new trace lifecycle without preserving existing identifiers.
 *
 * @see Tracer#create(ImmutableList, ImmutableMap, ImmutableList)
 * @see Tracer#start()
 * @see Tracer#start(Function)
 */
@FunctionalInterface
public interface Generator {

    String generate();

}
