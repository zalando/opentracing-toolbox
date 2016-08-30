package org.zalando.tracer.spring;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.zalando.tracer.Trace;
import org.zalando.tracer.Tracer;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.assertThat;

@ContextConfiguration(classes = AspectTest.TestConfiguration.class)
@ActiveProfiles("uuid")
public class AspectTest extends AbstractTest {

    @Import(TracedService.class)
    public static class TestConfiguration {

        @Bean
        public Trace trace(final Tracer tracer) {
            return tracer.get("X-Trace-ID");
        }

    }

    @Autowired
    private TracedService service;

    @Test
    public void shouldTrace() {
        final String trace = service.withAspect();
        assertThat(trace, is(notNullValue()));
    }

}
