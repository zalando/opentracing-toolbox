package org.zalando.tracer.spring;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.zalando.tracer.Trace;
import org.zalando.tracer.Tracer;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeoutException;

import static java.util.concurrent.TimeUnit.MILLISECONDS;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@ImportAutoConfiguration(TracerAutoConfiguration.class)
@ActiveProfiles("uuid")
class SchedulingTest {

    @SpringBootConfiguration
    @EnableAutoConfiguration
    @EnableScheduling
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

    @Autowired
    private ScheduledThreadPoolExecutor taskSchedulerService;

    @Test
    void shouldScheduleWithTracer() throws InterruptedException, ExecutionException, TimeoutException {
        final String value = future.get(100, MILLISECONDS);

        assertThat(value, is(notNullValue()));
    }

    @Test
    void shouldConfigureCorePoolSize() {
        assertThat(taskSchedulerService.getCorePoolSize(), is(16));
    }

}
