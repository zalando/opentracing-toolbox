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

import org.junit.Before;
import org.junit.Test;
import org.zalando.tracer.Trace;
import org.zalando.tracer.Tracer;

import java.util.concurrent.atomic.AtomicReference;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

public class TraceAspectTest {

    private final Tracer tracer = Tracer.builder()
            .trace("X-Trace-ID", () -> "f09f5896-73fd-11e5-bc6f-10ddb1ee7671")
            .build();

    private final Trace trace = tracer.get("X-Trace-ID");

    private final AtomicReference<String> capture = new AtomicReference<>();

    @Before
    public void setUp() throws Exception {
        
    }

    @Traced
    public void perform() {
        capture.set(trace.getValue());
    }

    @Test
    public void shouldStartTracer() {
        perform();

        assertThat(capture.get(), is("f09f5896-73fd-11e5-bc6f-10ddb1ee7671"));
    }

}
