package org.zalando.tracer.spring;

import io.opentracing.Tracer;
import io.opentracing.mock.MockTracer;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
class Application {

    @Bean
    public Tracer tracer() {
        return new MockTracer();
    }

}
