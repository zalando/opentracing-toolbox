package org.zalando.tracer.spring;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.AsyncResult;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.stereotype.Service;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.zalando.tracer.Trace;
import org.zalando.tracer.Tracer;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeoutException;

import static java.util.concurrent.TimeUnit.MILLISECONDS;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@ImportAutoConfiguration(TracerAutoConfiguration.class)
@ActiveProfiles("uuid")
class AsyncTest {

    @SpringBootConfiguration
    @EnableAutoConfiguration
    @EnableAsync
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
    void shouldPerformAsyncTaskWithTracer() throws InterruptedException, ExecutionException, TimeoutException {
        tracer.start();

        try {
            final String value = service.getTraceId().get(500, MILLISECONDS);

            assertThat(value, is(notNullValue()));
        } finally {
            tracer.stop();
        }
    }

    @Test
    void shouldPerformAsyncTaskWithoutTracer() throws InterruptedException, ExecutionException, TimeoutException {
        final String value = service.getTraceId().get(500, MILLISECONDS);

        assertThat(value, is(notNullValue()));
    }

}
