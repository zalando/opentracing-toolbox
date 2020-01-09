package org.zalando.opentracing.jdbc.autoconfigure;

import io.opentracing.mock.MockTracer;
import org.h2.jdbcx.JdbcDataSource;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.aop.support.AopUtils.isAopProxy;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.NONE;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = NONE)
@EnableAutoConfiguration
class ProxyTest {

    @Configuration
    static class TestConfiguration {

        @Bean
        public MockTracer tracer() {
            return new MockTracer();
        }

    }

    @Autowired
    private JdbcDataSource dataSource;

    @Test
    void proxiesDataSource() {
        assertTrue(isAopProxy(dataSource));
    }

    @Test
    void supportsReadAccess() {
        dataSource.getURL();
    }

    @Test
    void supportsWriteAccess() {
        dataSource.setLoginTimeout(1);
    }

}
