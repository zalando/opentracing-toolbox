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

import org.junit.Test;
import org.zalando.tracer.Tracer;

public final class TracerRuleBadCaseTest {

    private final Tracer tracer = Tracer.create("X-Trace-ID");

    @Test(expected = IllegalStateException.class)
    public void shouldManageTrace() {
        tracer.get("X-Trace-ID").getValue();
    }

}