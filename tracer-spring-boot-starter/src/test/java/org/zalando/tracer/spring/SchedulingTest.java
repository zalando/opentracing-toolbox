package org.zalando.tracer.spring;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.zalando.tracer.Trace;
import org.zalando.tracer.Tracer;

import java.util.concurrent.CompletableFuture;
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

    @EnableAutoConfiguration
    @Configuration
    public static class TestConfiguration {

        private final CompletableFuture<String> future = new CompletableFuture<>();

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
            future.complete(trace.getValue());
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
