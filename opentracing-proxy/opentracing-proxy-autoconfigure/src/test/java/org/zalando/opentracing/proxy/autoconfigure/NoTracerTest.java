package org.zalando.opentracing.proxy.autoconfigure;

import io.opentracing.Tracer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@EnableAutoConfiguration
class NoTracerTest {

    @Configuration
    static class TestConfiguration {

    }

    @Autowired(required = false)
    private Tracer tracer;

    @Test
    void configuresNoProxyTracer() {
        assertThat(tracer, is(nullValue()));
    }

}
