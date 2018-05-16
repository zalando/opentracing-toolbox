package org.zalando.tracer.spring;

import org.apiguardian.api.API;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Primary;
import org.springframework.core.Ordered;
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.scheduling.annotation.SchedulingConfiguration;
import org.springframework.scheduling.annotation.SchedulingConfigurer;
import org.springframework.scheduling.concurrent.ConcurrentTaskExecutor;
import org.springframework.scheduling.concurrent.ConcurrentTaskScheduler;
import org.springframework.scheduling.config.ScheduledTaskRegistrar;
import org.zalando.tracer.LoggingTraceListener;
import org.zalando.tracer.MDCTraceListener;
import org.zalando.tracer.StackedMDCTraceListener;
import org.zalando.tracer.TraceListener;
import org.zalando.tracer.Tracer;
import org.zalando.tracer.TracerFactory;
import org.zalando.tracer.aspectj.TracedAspect;
import org.zalando.tracer.httpclient.TracerHttpRequestInterceptor;
import org.zalando.tracer.servlet.TracerFilter;

import javax.servlet.Filter;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;

import static java.util.Collections.emptyList;
import static java.util.concurrent.Executors.newCachedThreadPool;
import static javax.servlet.DispatcherType.ASYNC;
import static javax.servlet.DispatcherType.REQUEST;
import static org.apiguardian.api.API.Status.INTERNAL;
import static org.apiguardian.api.API.Status.STABLE;
import static org.springframework.aop.interceptor.AsyncExecutionAspectSupport.DEFAULT_TASK_EXECUTOR_BEAN_NAME;
import static org.springframework.scheduling.annotation.ScheduledAnnotationBeanPostProcessor.DEFAULT_TASK_SCHEDULER_BEAN_NAME;
import static org.zalando.tracer.concurrent.TracingExecutors.manage;
import static org.zalando.tracer.concurrent.TracingExecutors.tryPreserve;

@API(status = STABLE)
@Configuration
@ConditionalOnClass(Tracer.class)
@EnableConfigurationProperties(TracerProperties.class)
@AutoConfigureAfter(name = {
        "org.springframework.boot.autoconfigure.web.WebMvcAutoConfiguration", // Spring Boot 1.x
        "org.springframework.boot.autoconfigure.web.servlet.WebMvcAutoConfiguration" // Spring Boot 2.x
})
@Import({DefaultGeneratorResolver.class, TracerAutoConfiguration.AspectConfiguration.class})
public class TracerAutoConfiguration {

    public static final String FILTER_NAME = "tracerFilter";

    // IDEA doesn't support @EnableConfigurationProperties
    private final TracerProperties properties;
    private final GeneratorResolver resolver;

    @API(status = INTERNAL)
    @Autowired
    public TracerAutoConfiguration(final TracerProperties properties, final GeneratorResolver resolver) {
        this.properties = properties;
        this.resolver = resolver;
    }

    @API(status = INTERNAL)
    @Bean
    @ConditionalOnWebApplication
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

    @API(status = INTERNAL)
    @Bean
    @ConditionalOnMissingBean(TracerHttpRequestInterceptor.class)
    public TracerHttpRequestInterceptor tracerHttpRequestInterceptor(final Tracer tracer) {
        return new TracerHttpRequestInterceptor(tracer);
    }

    @API(status = INTERNAL)
    @ConditionalOnClass(Aspect.class)
    @ConditionalOnProperty(name = "tracer.aspect.enabled", havingValue = "true", matchIfMissing = true)
    @EnableAspectJAutoProxy
    public static class AspectConfiguration {

        @Bean
        public TracedAspect tracedAspect(final Tracer tracer) {
            final TracedAspect aspect = new TracedAspect();
            aspect.setTracer(tracer);
            return aspect;
        }

    }

    @API(status = INTERNAL)
    @Bean
    public Tracer tracer(
            @SuppressWarnings({"SpringJavaAutowiringInspection", "OptionalUsedAsFieldOrParameterType"})
            final Optional<List<TraceListener>> listeners) {
        final TracerFactory.Builder builder = Tracer.builder()
                .stacked(properties.isStacked())
                .listeners(listeners.orElse(emptyList()));

        final Map<String, String> traces = properties.getTraces();

        traces.forEach((name, type) ->
                builder.trace(name, resolver.resolve(type)));

        return builder.build();
    }

    @API(status = INTERNAL)
    @Bean
    @ConditionalOnProperty(name = "tracer.mdc.enabled", havingValue = "true", matchIfMissing = true)
    public TraceListener mdcTraceListener() {
        return properties.isStacked() ?
                new StackedMDCTraceListener() :
                new MDCTraceListener();
    }

    @API(status = INTERNAL)
    @Bean
    @ConditionalOnProperty(name = "tracer.logging.enabled", havingValue = "true")
    public LoggingTraceListener loggingTraceListener() {
        final String category = properties.getLogging().getCategory();
        return category == null ?
                new LoggingTraceListener() :
                new LoggingTraceListener(LoggerFactory.getLogger(category));
    }

    @Configuration
    @ConditionalOnClass(Scheduled.class)
    @ConditionalOnProperty(name = "tracer.scheduling.enabled", havingValue = "true", matchIfMissing = true)
    @AutoConfigureAfter(SchedulingConfiguration.class)
    static class SchedulingAutoConfiguration implements SchedulingConfigurer {

        /**
         * Needed because we require it inside {@link #configureTasks(ScheduledTaskRegistrar)} already.
         */
        @Configuration
        static class TaskSchedulerConfiguration {

            @Bean(destroyMethod = "shutdown")
            @ConditionalOnMissingBean(name = "taskSchedulerService")
            public ScheduledExecutorService taskSchedulerService(
                    @Value("${tracer.scheduling.pool-size:0}") final int poolSize) {
                final int corePoolSize = poolSize > 0 ? poolSize : Runtime.getRuntime().availableProcessors();
                return new ScheduledThreadPoolExecutor(corePoolSize);
            }

            @Bean(name = DEFAULT_TASK_SCHEDULER_BEAN_NAME)
            @ConditionalOnMissingBean(name = DEFAULT_TASK_SCHEDULER_BEAN_NAME)
            public TaskScheduler taskScheduler(final ScheduledExecutorService taskSchedulerService, final Tracer tracer) {
                return new ConcurrentTaskScheduler(manage(taskSchedulerService, tracer));
            }

        }

        private final TaskScheduler scheduler;

        @Autowired
        public SchedulingAutoConfiguration(
                @Qualifier(DEFAULT_TASK_SCHEDULER_BEAN_NAME) final TaskScheduler scheduler) {
            this.scheduler = scheduler;
        }

        @Override
        public void configureTasks(final ScheduledTaskRegistrar registrar) {
            registrar.setTaskScheduler(scheduler);
        }

    }

    @Configuration
    @ConditionalOnClass(Async.class)
    @ConditionalOnProperty(name = "tracer.async.enabled", havingValue = "true", matchIfMissing = true)
    static class AsyncAutoConfiguration {

        @Bean(destroyMethod = "shutdown")
        @ConditionalOnMissingBean(name = "taskExecutorService")
        public ExecutorService taskExecutorService() {
            return newCachedThreadPool();
        }

        @Primary
        @Bean(name = DEFAULT_TASK_EXECUTOR_BEAN_NAME)
        @ConditionalOnMissingBean(name = DEFAULT_TASK_EXECUTOR_BEAN_NAME)
        public TaskExecutor taskExecutor(final ExecutorService taskExecutorService, final Tracer tracer) {
            return new ConcurrentTaskExecutor(tryPreserve(taskExecutorService, tracer));
        }

    }

}
