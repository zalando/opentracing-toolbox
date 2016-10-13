package org.zalando.tracer.spring;

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

import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.TimeoutException;

import static java.util.concurrent.Executors.newSingleThreadExecutor;
import static java.util.concurrent.TimeUnit.MILLISECONDS;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.assertThat;

@ContextConfiguration(classes = AsyncInstrumentTest.TestConfiguration.class)
@ActiveProfiles("uuid")
public class AsyncInstrumentTest extends AbstractTest {

    @EnableAutoConfiguration
    @EnableAsync
    @Configuration
    @Import(TaskService.class)
    public static class TestConfiguration {

        @Bean
        public Trace trace(final Tracer tracer) {
            return tracer.get("X-Trace-ID");
        }

        @Bean
        public ExecutorService taskExecutorService() {
            return newSingleThreadExecutor();
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
