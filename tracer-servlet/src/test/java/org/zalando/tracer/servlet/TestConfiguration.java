package org.zalando.tracer.servlet;

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

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.zalando.tracer.FlowIDGenerator;
import org.zalando.tracer.Generator;
import org.zalando.tracer.Tracer;
import org.zalando.tracer.UUIDGenerator;

import static org.mockito.Mockito.spy;

@Configuration
@EnableWebMvc
@Import(ExampleController.class)
public class TestConfiguration {

    @Bean
    public Generator uuidGenerator() {
        return spy(new MockGenerator(new UUIDGenerator()));
    }

    @Bean
    public Generator flowIDGenerator() {
        return spy(new MockGenerator(new FlowIDGenerator()));
    }

    @Bean
    public Tracer tracer(final Generator uuidGenerator, final Generator flowIDGenerator) {
        return Tracer.builder()
                .trace("X-Trace-ID", uuidGenerator)
                .trace("X-Flow-ID", flowIDGenerator)
                .build();
    }

    @Bean
    public TracerFilter tracerFilter(final Tracer tracer) {
        return new TracerFilter(tracer);
    }

    @Bean
    public MockMvc mockMvc(final WebApplicationContext context, final TracerFilter tracerFilter) {
        return MockMvcBuilders.webAppContextSetup(context)
                .addFilter(tracerFilter, "/traced", "/traced-async", "/traced-forward")
                .build();
    }

}
