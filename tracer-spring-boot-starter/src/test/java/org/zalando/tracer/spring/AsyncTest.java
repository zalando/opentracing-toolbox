package org.zalando.tracer.spring;

/*
 * ⁣​
 * Tracer: Spring Boot Starter
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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.AsyncResult;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.stereotype.Service;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.zalando.tracer.Trace;
import org.zalando.tracer.Tracer;
import org.zalando.tracer.aspectj.Traced;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeoutException;

import static java.util.concurrent.TimeUnit.MILLISECONDS;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.assertThat;

@ContextConfiguration(classes = AsyncTest.TestConfiguration.class)
@ActiveProfiles("uuid")
public class AsyncTest extends AbstractTest {

    @EnableAutoConfiguration
    @EnableAsync
    @Configuration
    @Import(TaskService.class)
    public static class TestConfiguration {

        @Bean
        public Trace trace(final Tracer tracer) {
            return tracer.get("X-Trace-ID");
        }

    }

    @Service
    static class TaskService {

        @Autowired
        private Trace trace;

        @Async
        public Future<String> getTraceId() {
            return new AsyncResult<>(trace.getValue());
        }

    }

    @Autowired
    private Tracer tracer;

    @Autowired
    private TaskService service;

    @Test
    public void shouldPerformAsyncTaskWithTracer() throws InterruptedException, ExecutionException, TimeoutException {
        tracer.start();

        try {
            final String value = service.getTraceId().get(500, MILLISECONDS);

            assertThat(value, is(notNullValue()));
        } finally {
            tracer.stop();
        }

    }

}
