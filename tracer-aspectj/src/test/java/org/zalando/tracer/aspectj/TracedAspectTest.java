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

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.zalando.tracer.Trace;
import org.zalando.tracer.Tracer;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = TracedAspectTest.TestConfiguration.class)
public class TracedAspectTest {

    @Configuration
    @Import(TracedService.class)
    @EnableAspectJAutoProxy
    public static class TestConfiguration {

        @Bean
        public Tracer tracer() {
            return Tracer.builder()
                    .trace("X-Trace-ID", () -> "f09f5896-73fd-11e5-bc6f-10ddb1ee7671")
                    .build();
        }

        @Bean
        public TracedAspect tracedAspect(final Tracer tracer) {
            final TracedAspect aspect = new TracedAspect();
            aspect.setTracer(tracer);
            return aspect;
        }

        @Bean
        public Trace trace(final Tracer tracer) {
            return tracer.get("X-Trace-ID");
        }

    }

    @Rule
    public final ExpectedException exception = ExpectedException.none();

    @Autowired
    private TracedService service;

    @Autowired
    private Trace trace;

    @Test
    public void shouldStartTracer() {
        assertThat(service.withAspect(), is("f09f5896-73fd-11e5-bc6f-10ddb1ee7671"));
    }

    @Test
    public void shouldStopTracer() {
        service.withAspect();

        exception.expect(IllegalStateException.class);
        trace.getValue();
    }

    @Test
    public void shouldNotStartTracer() {
        exception.expect(IllegalStateException.class);
        service.withoutAspect();
    }

}
