package org.zalando.opentracing.servlet.extension.autoconfigure;

import io.opentracing.contrib.web.servlet.filter.ServletFilterSpanDecorator;
import org.apiguardian.api.API;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.zalando.opentracing.servlet.extension.StandardSpanDecorator;

import static org.apiguardian.api.API.Status.EXPERIMENTAL;
import static org.apiguardian.api.API.Status.INTERNAL;

@API(status = EXPERIMENTAL)
@Configuration
@ConditionalOnWebApplication
@ConditionalOnClass(ServletFilterSpanDecorator.class)
@AutoConfigureAfter(name =
        "io.opentracing.contrib.spring.tracer.configuration.TracerAutoConfiguration")
@AutoConfigureBefore(name =
        "io.opentracing.contrib.spring.web.starter.ServerTracingAutoConfiguration")
public class OpenTracingServletExtensionAutoConfiguration {

    @API(status = INTERNAL)
    @Bean
    @ConditionalOnMissingBean(ServletFilterSpanDecorator.class)
    public ServletFilterSpanDecorator standardServletFilterSpanDecorator() {
        return new StandardSpanDecorator();
    }

}
