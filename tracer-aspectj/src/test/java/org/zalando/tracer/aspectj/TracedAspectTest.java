package org.zalando.tracer.aspectj;

/*
 * ⁣​
 * Tracer: AspectJ
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

import org.aspectj.lang.Aspects;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.zalando.tracer.Trace;
import org.zalando.tracer.Tracer;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

public final class TracedAspectTest {

    @Rule
    public final ExpectedException exception = ExpectedException.none();

    private final Tracer tracer = Tracer.builder()
            .trace("X-Trace-ID", () -> "f09f5896-73fd-11e5-bc6f-10ddb1ee7671")
            .build();

    private final Trace trace = tracer.get("X-Trace-ID");


    @Before
    public void setUp() {
        Aspects.aspectOf(TracedAspect.class).setTracer(tracer);
    }

    @Traced
    public String withAspect() {
        return trace.getValue();
    }

    public String withoutAspect() {
        return trace.getValue();
    }

    @Test
    public void shouldStartTracer() {
        assertThat(withAspect(), is("f09f5896-73fd-11e5-bc6f-10ddb1ee7671"));
    }

    @Test
    public void shouldStopTracer() {
        withAspect();

        exception.expect(IllegalStateException.class);
        trace.getValue();
    }

    @Test
    public void shouldNotStartTracer() {
        exception.expect(IllegalStateException.class);
        withoutAspect();
    }

}
