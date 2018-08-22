package org.zalando.tracer.spring;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.nullValue;

class FilterTest {

    @ExtendWith(SpringExtension.class)
    @SpringBootTest(classes = Application.class, properties = "spring.main.web-application-type=NONE")
    @ImportAutoConfiguration(TracerWebMvcAutoConfiguration.class)
    static class NonWebEnvironmentTest {

        @Autowired(required = false)
        @Qualifier("tracerFilter")
        private FilterRegistrationBean tracerFilter;

        @Test
        void shouldNotInitializeFilter() {
            assertThat(tracerFilter, is(nullValue()));
        }
    }

    @ExtendWith(SpringExtension.class)
    @SpringBootTest(classes = Application.class, properties = "spring.main.web-application-type=REACTIVE")
    @ImportAutoConfiguration(TracerWebMvcAutoConfiguration.class)
    static class WebFluxEnvironmentTest {

        @Autowired(required = false)
        @Qualifier("tracerFilter")
        private FilterRegistrationBean tracerFilter;

        @Test
        void shouldNotInitializeFilter() {
            assertThat(tracerFilter, is(nullValue()));
        }
    }

    @ExtendWith(SpringExtension.class)
    @SpringBootTest(classes = Application.class, properties = "spring.main.web-application-type=SERVLET")
    @ImportAutoConfiguration(TracerWebMvcAutoConfiguration.class)
    static class WebMvcEnvironmentTest {

        @Autowired
        @Qualifier("tracerFilter")
        private FilterRegistrationBean tracerFilter;

        @Test
        void shouldInitializeFilter() {
            assertThat(tracerFilter, is(notNullValue()));
        }
    }

}
