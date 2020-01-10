package org.zalando.opentracing.proxy.autoconfigure;

import io.opentracing.Tracer;
import org.apiguardian.api.API;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.condition.ConditionalOnSingleCandidate;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.zalando.opentracing.proxy.core.ProxyTracer;
import org.zalando.opentracing.proxy.intercept.name.Naming;
import org.zalando.opentracing.proxy.intercept.name.Rename;
import org.zalando.opentracing.proxy.plugin.AutoTagging;
import org.zalando.opentracing.proxy.plugin.LogCorrelation;
import org.zalando.opentracing.proxy.plugin.TagPropagation;
import org.zalando.opentracing.proxy.spi.Plugin;

import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;

import static java.util.function.Function.identity;
import static java.util.stream.Collectors.toMap;
import static org.apiguardian.api.API.Status.INTERNAL;
import static org.apiguardian.api.API.Status.STABLE;

@API(status = STABLE)
@Configuration
@EnableConfigurationProperties(ProxyProperties.class)
@AutoConfigureAfter(name = "io.opentracing.contrib.spring.tracer.configuration.TracerAutoConfiguration")
@AutoConfigureBefore(name = "io.opentracing.contrib.spring.tracer.configuration.TracerRegisterAutoConfiguration")
public class OpenTracingProxyAutoConfiguration {

    @API(status = INTERNAL)
    @Bean
    @ConditionalOnMissingBean(AutoTagging.class)
    @ConditionalOnProperty(
            name = "opentracing.proxy.auto-tagging.enabled",
            havingValue = "true",
            matchIfMissing = true)
    public AutoTagging autoTagging(final ProxyProperties properties) {
        final List<String> keys = properties.getAutoTagging().getKeys();
        final Map<String, String> mapping = keys.stream()
                .collect(toMap(identity(), identity()));

        return new AutoTagging(mapping);
    }

    @API(status = INTERNAL)
    @Bean
    @ConditionalOnMissingBean(LogCorrelation.class)
    @ConditionalOnProperty(
            name = "opentracing.proxy.log-correlation.enabled",
            havingValue = "true",
            matchIfMissing = true)
    public LogCorrelation logCorrelation(final ProxyProperties properties) {
        return new LogCorrelation()
                .withTraceId(properties.getLogCorrelation().getTraceId())
                .withSpanId(properties.getLogCorrelation().getSpanId())
                .withBaggage(properties.getLogCorrelation().getBaggage());
    }

    @API(status = INTERNAL)
    @Bean
    @ConditionalOnMissingBean(Naming.class)
    @ConditionalOnProperty(
            name = "opentracing.proxy.rename.enabled",
            havingValue = "true",
            matchIfMissing = true)
    public Rename rename(final ProxyProperties properties) {
        return new Rename(properties.getRename().getFormat());
    }

    @API(status = INTERNAL)
    @Bean
    @ConditionalOnMissingBean(TagPropagation.class)
    @ConditionalOnProperty(
            name = "opentracing.proxy.tag-propagation.enabled",
            havingValue = "true",
            matchIfMissing = true)
    public TagPropagation tagPropagation(final ProxyProperties properties) {
        return new TagPropagation(properties.getTagPropagation().getKeys());
    }

    @API(status = INTERNAL)
    @Bean
    @Primary
    @ConditionalOnSingleCandidate(Tracer.class)
    public Tracer proxyTracer(
            final Tracer tracer, final List<Plugin> plugins) {

        return reduce(plugins, new ProxyTracer(tracer), ProxyTracer::with);
    }

    private static <E, R> R reduce(
            final Iterable<E> elements,
            final R identity,
            final BiFunction<R, E, R> accumulator) {

        R result = identity;

        for (final E element : elements) {
            result = accumulator.apply(result, element);
        }

        return result;
    }

}
