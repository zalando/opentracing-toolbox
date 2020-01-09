package org.zalando.opentracing.jdbc.autoconfigure;

import io.opentracing.mock.MockTracer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import javax.sql.DataSource;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.springframework.aop.support.AopUtils.isAopProxy;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.NONE;

@ExtendWith(SpringExtension.class)
@SpringBootTest(
        webEnvironment = NONE,
        properties = "opentracing.jdbc.enabled: false")
@EnableAutoConfiguration
class DisabledTest {

    @Configuration
    static class TestConfiguration {

        @Bean
        public MockTracer tracer() {
            return new MockTracer();
        }

    }

    @Autowired
    private DataSource dataSource;

    @Test
    void wontProxyDataSourceWhenDisabled() {
        assertFalse(isAopProxy(dataSource));
    }

}
