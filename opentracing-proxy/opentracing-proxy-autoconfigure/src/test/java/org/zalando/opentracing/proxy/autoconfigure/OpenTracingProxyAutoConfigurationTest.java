package org.zalando.opentracing.proxy.autoconfigure;

import io.opentracing.Tracer;
import io.opentracing.mock.MockTracer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.zalando.opentracing.proxy.core.ProxyTracer;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.is;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@EnableAutoConfiguration
class OpenTracingProxyAutoConfigurationTest {

    @Configuration
    static class TestConfiguration {

        @Bean
        public MockTracer mockTracer() {
            return new MockTracer();
        }

    }

    @Autowired
    private Tracer tracer;

    @Test
    void configuresProxyTracer() {
        assertThat(tracer, is(instanceOf(ProxyTracer.class)));
    }

}
