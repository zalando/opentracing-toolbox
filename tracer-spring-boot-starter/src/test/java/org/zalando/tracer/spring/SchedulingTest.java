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

import com.google.common.util.concurrent.SettableFuture;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.zalando.tracer.Trace;
import org.zalando.tracer.Tracer;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeoutException;

import static java.util.concurrent.TimeUnit.MILLISECONDS;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.assertThat;

@ContextConfiguration(classes = SchedulingTest.TestConfiguration.class)
@ActiveProfiles("uuid")
public class SchedulingTest extends AbstractTest {

    @Configuration
    public static class TestConfiguration {

        private final SettableFuture<String> future = SettableFuture.create();

        @Autowired
        private Trace trace;

        @Bean
        public Trace trace(final Tracer tracer) {
            return tracer.get("X-Trace-ID");
        }

        @Bean
        public Future<String> future() {
            return future;
        }

        @Scheduled(fixedDelay = 1)
        public void scheduledFixedDelay() {
            future.set(trace.getValue());
        }

    }

    @Autowired
    private Future<String> future;

    @Test
    public void shouldScheduleWithTracer() throws InterruptedException, ExecutionException, TimeoutException {
        final String value = future.get(100, MILLISECONDS);

        assertThat(value, is(notNullValue()));
    }

}
