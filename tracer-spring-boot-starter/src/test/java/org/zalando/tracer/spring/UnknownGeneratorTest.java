package org.zalando.tracer.spring;

/*
 * ⁣​
 * Tracer: Servlet
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

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;
import org.zalando.tracer.FlowIDGenerator;
import org.zalando.tracer.Trace;
import org.zalando.tracer.Tracer;
import org.zalando.tracer.UUIDGenerator;

import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

public final class UnknownGeneratorTest {

    private final GeneratorResolver unit = new DefaultGeneratorResolver();

    @Test
    public void shouldSupportFlowId() {
        assertThat(unit.resolve("flow-id"), is(instanceOf(FlowIDGenerator.class)));
    }

    @Test
    public void shouldSupportUuid() {
        assertThat(unit.resolve("uuid"), is(instanceOf(UUIDGenerator.class)));
    }

    @Test
    public void shouldSupportFlowIdCaseInsensitive() {
        assertThat(unit.resolve("Flow-ID"), is(instanceOf(FlowIDGenerator.class)));
    }

    @Test
    public void shouldSupportUuidCaseInsensitive() {
        assertThat(unit.resolve("UUID"), is(instanceOf(UUIDGenerator.class)));
    }

    @Test(expected = UnsupportedOperationException.class)
    public void shouldNotSupportUnknown() {
        unit.resolve("unknown");
    }

}
