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
import org.springframework.context.annotation.Primary;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.everyItem;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.is;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@EnableAutoConfiguration
class MultipleTracerTest {

    @Configuration
    static class TestConfiguration {

        @Bean
        @Primary
        public MockTracer tracer1() {
            return new MockTracer();
        }

        @Bean
        @Primary
        public MockTracer tracer2() {
            return new MockTracer();
        }

    }

    @Autowired
    private List<Tracer> tracers;

    @Test
    void configuresNoProxyTracer() {
        assertThat(tracers, hasSize(2));
        assertThat(tracers, everyItem(is(instanceOf(MockTracer.class))));
    }

}
