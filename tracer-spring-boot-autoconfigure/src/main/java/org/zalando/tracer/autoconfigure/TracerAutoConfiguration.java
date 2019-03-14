package org.zalando.tracer.autoconfigure;

import io.opentracing.Tracer;
import io.opentracing.contrib.api.tracer.APIExtensionsTracer;
import org.apiguardian.api.API;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.Ordered;
import org.zalando.tracer.Flow;
import org.zalando.tracer.MDCSpanObserver;
import org.zalando.tracer.httpclient.FlowHttpRequestInterceptor;
import org.zalando.tracer.servlet.FlowFilter;

import javax.servlet.Filter;

import static javax.servlet.DispatcherType.REQUEST;
import static org.apiguardian.api.API.Status.INTERNAL;
import static org.apiguardian.api.API.Status.STABLE;

@API(status = STABLE)
@Configuration
@ConditionalOnBean(Tracer.class)
@AutoConfigureAfter(name = {
        "org.springframework.boot.autoconfigure.web.WebMvcAutoConfiguration", // Spring Boot 1.x
        "org.springframework.boot.autoconfigure.web.servlet.WebMvcAutoConfiguration", // Spring Boot 2.x
        "io.opentracing.contrib.spring.web.starter.ServerTracingAutoConfiguration"
})
public class TracerAutoConfiguration {

    @API(status = INTERNAL)
    @Bean
    public Flow flow(final Tracer tracer) {
        return Flow.create(tracer);
    }

    @API(status = INTERNAL)
    @Bean
    @ConditionalOnMissingBean(FlowHttpRequestInterceptor.class)
    public FlowHttpRequestInterceptor flowHttpRequestInterceptor(final Flow flow) {
        return new FlowHttpRequestInterceptor(flow);
    }

    @API(status = INTERNAL)
    @Bean
    @Primary
    @ConditionalOnProperty(name = "tracer.mdc.enabled", havingValue = "true", matchIfMissing = true)
    public Tracer mdcTracer(final Tracer tracer) {
        final APIExtensionsTracer extensionsTracer = new APIExtensionsTracer(tracer);
        extensionsTracer.addTracerObserver(new MDCSpanObserver());
        return extensionsTracer;
    }

    @Configuration
    @ConditionalOnClass({Filter.class, FilterRegistrationBean.class})
    @ConditionalOnWebApplication
    @ConditionalOnProperty(name = "tracer.filter.enabled", havingValue = "true", matchIfMissing = true)
    static class TracerWebMvcAutoConfiguration {

        public static final String FILTER_NAME = "flowFilter";

        @API(status = INTERNAL)
        @Bean
        @ConditionalOnMissingBean(name = FILTER_NAME)
        public FilterRegistrationBean flowFilter(final Flow flow) {
            final Filter filter = new FlowFilter(flow);
            @SuppressWarnings("unchecked") // as of Spring Boot 2.x
            final FilterRegistrationBean registration = new FilterRegistrationBean(filter);
            registration.setName(FILTER_NAME);
            registration.setDispatcherTypes(REQUEST);
            // see https://github.com/opentracing-contrib/java-spring-web/blob/master/opentracing-spring-web-starter/src/main/java/io/opentracing/contrib/spring/web/starter/WebTracingProperties.java
            registration.setOrder(Ordered.HIGHEST_PRECEDENCE + 1);
            return registration;
        }

    }

}
