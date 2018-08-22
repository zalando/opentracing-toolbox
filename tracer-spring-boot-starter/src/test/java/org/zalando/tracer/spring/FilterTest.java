package org.zalando.tracer.spring;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.core.SpringVersion;
import org.springframework.test.context.BootstrapWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.util.ClassUtils;

import javax.servlet.Filter;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.nullValue;

class FilterTest {

    @ExtendWith(SpringExtension.class)
    @SpringBootTest(classes = Application.class)
    @ImportAutoConfiguration(TracerAutoConfiguration.class)
    static class FilterNotOnClasspathTest {

        @Autowired(required = false)
        @Qualifier("tracerFilter")
        private FilterRegistrationBean tracerFilter;

        @Test
        void shouldNotInitializeFilter() {
            assertThat(tracerFilter, is(nullValue()));
        }
    }

    @ExtendWith(SpringExtension.class)
    @SpringBootTest(classes = Application.class)
    @ImportAutoConfiguration(TracerWebMvcAutoConfiguration.class)
    static class FilterOnClasspathTest {

        @Autowired
        @Qualifier("tracerFilter")
        private FilterRegistrationBean tracerFilter;

        @Test
        void shouldInitializeFilter() {
            assertThat(tracerFilter, is(notNullValue()));
        }
    }
}
