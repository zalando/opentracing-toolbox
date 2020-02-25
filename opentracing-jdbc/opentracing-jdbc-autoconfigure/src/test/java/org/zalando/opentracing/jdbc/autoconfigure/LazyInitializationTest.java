package org.zalando.opentracing.jdbc.autoconfigure;

import io.opentracing.mock.MockTracer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Role;
import org.springframework.core.Ordered;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.zalando.opentracing.jdbc.Activation;
import org.zalando.opentracing.jdbc.DataSourceTracer;
import org.zalando.opentracing.jdbc.Lifecycle;
import org.zalando.opentracing.jdbc.operation.OperationName;
import org.zalando.opentracing.jdbc.span.SpanDecorator;

import java.util.HashSet;
import java.util.Set;

import static com.google.common.collect.Sets.newHashSet;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.NONE;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = NONE)
@EnableAutoConfiguration
class LazyInitializationTest {

    // unordered post processors come last
    static class ReportingBeanPostProcessor implements BeanPostProcessor {

        private final Set<Class<?>> processedTypes = new HashSet<>();

        @Override
        public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
            return bean;
        }

        @Override
        public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
            processedTypes.add(bean.getClass());
            return bean;
        }
    }

    @Configuration
    static class TestConfiguration {

        @Bean
        @Role(BeanDefinition.ROLE_INFRASTRUCTURE)
        static BeanPostProcessor markerProcessor() {
            return new ReportingBeanPostProcessor();
        }

        @Bean
        public MockTracer tracer() {
            return new MockTracer();
        }

    }

    @Autowired
    private ReportingBeanPostProcessor reporter;

    @Test
    void wontEarlyInitializeTracingBeans() {
        final Set<Class<?>> expectedTypes = newHashSet(
                DataSourceTracer.class,
                SpanDecorator.class,
                Activation.class,
                Lifecycle.class,
                OperationName.class
        );

        expectedTypes.forEach(type ->
                assertFalse(isEarlyInitialized(type), String.format("Expected [%s] to be pre-processable", type)));
    }

    private boolean isEarlyInitialized(final Class<?> type) {
        return reporter.processedTypes.stream().noneMatch(type::isAssignableFrom);
    }
}
