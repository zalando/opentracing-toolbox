package org.zalando.tracer.spring;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.zalando.tracer.Trace;
import org.zalando.tracer.Tracer;

@ContextConfiguration(classes = DisabledAspectTest.TestConfiguration.class)
@TestPropertySource(properties = "tracer.aspect.enabled = false")
@ActiveProfiles("uuid")
public class DisabledAspectTest extends AbstractTest {

    @Import(TracedService.class)
    public static class TestConfiguration {

        @Bean
        public Trace trace(final Tracer tracer) {
            return tracer.get("X-Trace-ID");
        }

    }

    @Autowired
    private TracedService service;

    @Test(expected = IllegalStateException.class)
    public void shouldNotTrace() {
        service.withAspect();
    }

}
