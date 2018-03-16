package org.zalando.tracer.aspectj;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.zalando.tracer.Trace;
import org.zalando.tracer.Tracer;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = TracedAspectTest.TestConfiguration.class)
class TracedAspectTest {

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

    @Autowired
    private TracedService service;

    @Autowired
    private Trace trace;

    @Test
    void shouldStartTracer() {
        assertThat(service.withAspect(), is("f09f5896-73fd-11e5-bc6f-10ddb1ee7671"));
    }

    @Test
    void shouldFail() {
        assertThrows(UnsupportedOperationException.class, service::failWithAspect);
    }

    @Test
    void shouldStopTracer() {
        service.withAspect();

        assertThrows(IllegalStateException.class, trace::getValue);
    }

    @Test
    void shouldNotStartTracer() {
        assertThrows(IllegalStateException.class, service::withoutAspect);
    }

}
