package org.zalando.tracer.aspectj;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.zalando.tracer.Trace;
import org.zalando.tracer.Tracer;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = TracedAspectTest.TestConfiguration.class)
public class TracedAspectTest {

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

    @Rule
    public final ExpectedException exception = ExpectedException.none();

    @Autowired
    private TracedService service;

    @Autowired
    private Trace trace;

    @Test
    public void shouldStartTracer() {
        assertThat(service.withAspect(), is("f09f5896-73fd-11e5-bc6f-10ddb1ee7671"));
    }

    @Test(expected = UnsupportedOperationException.class)
    public void shouldFail() {
        service.failWithAspect();
    }

    @Test
    public void shouldStopTracer() {
        service.withAspect();

        exception.expect(IllegalStateException.class);
        trace.getValue();
    }

    @Test
    public void shouldNotStartTracer() {
        exception.expect(IllegalStateException.class);
        service.withoutAspect();
    }

}
