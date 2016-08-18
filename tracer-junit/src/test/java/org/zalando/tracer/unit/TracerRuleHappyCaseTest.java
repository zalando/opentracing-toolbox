package org.zalando.tracer.unit;

/*
 * ⁣​
 * Tracer: JUnit
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

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestRule;
import org.zalando.tracer.Tracer;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.assertThat;

public final class TracerRuleHappyCaseTest {

    private final Tracer tracer = Tracer.create("X-Trace-ID");

    @Rule
    public final TestRule tracing = new TracerRule(tracer);

    @Test
    public void shouldManageTrace() {
        assertThat(tracer.get("X-Trace-ID").getValue(), is(notNullValue()));
    }

}