package org.zalando.opentracing.spring.web.extension.autoconfigure;

import io.opentracing.contrib.spring.web.interceptor.HandlerInterceptorSpanDecorator;
import org.apiguardian.api.API;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.zalando.opentracing.spring.web.extension.StandardSpanDecorator;

import static org.apiguardian.api.API.Status.EXPERIMENTAL;
import static org.apiguardian.api.API.Status.INTERNAL;

@API(status = EXPERIMENTAL)
@Configuration
@ConditionalOnWebApplication
@ConditionalOnClass(StandardSpanDecorator.class)
@AutoConfigureAfter(name =
        "io.opentracing.contrib.spring.tracer.configuration.TracerAutoConfiguration")
@AutoConfigureBefore(name =
        "io.opentracing.contrib.spring.web.starter.ServerTracingAutoConfiguration")
public class OpenTracingWebExtensionAutoConfiguration {

    @API(status = INTERNAL)
    @Bean
    @ConditionalOnMissingBean(HandlerInterceptorSpanDecorator.class)
    public HandlerInterceptorSpanDecorator standardHandlerInterceptorSpanDecorator() {
        return new StandardSpanDecorator();
    }

}
