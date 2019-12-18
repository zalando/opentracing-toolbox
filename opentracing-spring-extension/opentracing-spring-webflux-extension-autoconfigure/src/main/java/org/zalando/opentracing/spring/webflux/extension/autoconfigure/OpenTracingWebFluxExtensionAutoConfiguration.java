package org.zalando.opentracing.spring.webflux.extension.autoconfigure;

import io.opentracing.contrib.spring.tracer.configuration.TracerAutoConfiguration;
import io.opentracing.contrib.spring.web.starter.WebFluxTracingAutoConfiguration;
import io.opentracing.contrib.spring.web.webfilter.WebFluxSpanDecorator;
import org.apiguardian.api.API;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.zalando.opentracing.spring.webflux.extension.StandardSpanDecorator;

import static org.apiguardian.api.API.Status.EXPERIMENTAL;
import static org.apiguardian.api.API.Status.INTERNAL;
import static org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication.Type.REACTIVE;

@API(status = EXPERIMENTAL)
@Configuration
@ConditionalOnWebApplication(type = REACTIVE)
@ConditionalOnClass(StandardSpanDecorator.class)
@AutoConfigureAfter(TracerAutoConfiguration.class)
@AutoConfigureBefore(WebFluxTracingAutoConfiguration.class)
public class OpenTracingWebFluxExtensionAutoConfiguration {

    @API(status = INTERNAL)
    @Bean
    @ConditionalOnMissingBean(WebFluxSpanDecorator.class)
    public WebFluxSpanDecorator standardWebFluxSpanDecorator() {
        return new StandardSpanDecorator();
    }

}
