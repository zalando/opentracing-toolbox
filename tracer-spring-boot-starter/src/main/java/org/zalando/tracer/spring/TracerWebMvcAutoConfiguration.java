package org.zalando.tracer.spring;

import org.apiguardian.api.API;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.core.Ordered;
import org.zalando.tracer.Tracer;
import org.zalando.tracer.servlet.TracerFilter;

import javax.servlet.Filter;

import static javax.servlet.DispatcherType.ASYNC;
import static javax.servlet.DispatcherType.REQUEST;
import static org.apiguardian.api.API.Status.INTERNAL;
import static org.apiguardian.api.API.Status.STABLE;

@API(status = STABLE)
@Configuration
@ConditionalOnClass({Tracer.class, Filter.class, FilterRegistrationBean.class})
@ConditionalOnWebApplication
@AutoConfigureAfter(name = {
        "org.springframework.boot.autoconfigure.web.WebMvcAutoConfiguration", // Spring Boot 1.x
        "org.springframework.boot.autoconfigure.web.servlet.WebMvcAutoConfiguration" // Spring Boot 2.x
})
@Import({TracerAutoConfiguration.class})
public class TracerWebMvcAutoConfiguration {

    public static final String FILTER_NAME = "tracerFilter";

    @API(status = INTERNAL)
    @Bean
    @ConditionalOnProperty(name = "tracer.filter.enabled", havingValue = "true", matchIfMissing = true)
    @ConditionalOnMissingBean(name = FILTER_NAME)
    public FilterRegistrationBean tracerFilter(final Tracer tracer) {
        final Filter filter = new TracerFilter(tracer);
        @SuppressWarnings("unchecked") // as of Spring Boot 2.x
        final FilterRegistrationBean registration = new FilterRegistrationBean(filter);
        registration.setName(FILTER_NAME);
        registration.setDispatcherTypes(REQUEST, ASYNC);
        registration.setOrder(Ordered.HIGHEST_PRECEDENCE);
        return registration;
    }

}
