package org.zalando.tracer.spring;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.scheduling.annotation.SchedulingConfiguration;
import org.springframework.scheduling.annotation.SchedulingConfigurer;
import org.springframework.scheduling.concurrent.ConcurrentTaskScheduler;
import org.springframework.scheduling.config.ScheduledTaskRegistrar;
import org.zalando.tracer.Tracer;

import java.util.concurrent.ScheduledExecutorService;

import static java.util.concurrent.Executors.newScheduledThreadPool;
import static org.springframework.scheduling.annotation.ScheduledAnnotationBeanPostProcessor.DEFAULT_TASK_SCHEDULER_BEAN_NAME;
import static org.zalando.tracer.concurrent.TracingExecutors.manage;

@Configuration
@ConditionalOnClass(Scheduled.class)
@ConditionalOnProperty(name = "tracer.scheduling.enabled", havingValue = "true", matchIfMissing = true)
@AutoConfigureAfter({TracerAutoConfiguration.class, SchedulingConfiguration.class})
@Import(TracerSchedulingAutoConfiguration.TaskSchedulerConfiguration.class)
public class TracerSchedulingAutoConfiguration implements SchedulingConfigurer {

    /**
     * Needed because we require it inside {@link #configureTasks(ScheduledTaskRegistrar)} already.
     */
    @Configuration
    @ConditionalOnMissingBean(name = DEFAULT_TASK_SCHEDULER_BEAN_NAME)
    public static class TaskSchedulerConfiguration {

        @Bean(destroyMethod = "shutdown")
        @ConditionalOnMissingBean(name = "taskSchedulerService")
        public ScheduledExecutorService taskSchedulerService() {
            return newScheduledThreadPool(Runtime.getRuntime().availableProcessors());
        }

        @Bean(name = DEFAULT_TASK_SCHEDULER_BEAN_NAME)
        public TaskScheduler taskScheduler(final ScheduledExecutorService taskSchedulerService, final Tracer tracer) {
            return new ConcurrentTaskScheduler(manage(taskSchedulerService, tracer));
        }

    }

    private final TaskScheduler scheduler;

    @Autowired
    public TracerSchedulingAutoConfiguration(
            @Qualifier(DEFAULT_TASK_SCHEDULER_BEAN_NAME) final TaskScheduler scheduler) {
        this.scheduler = scheduler;
    }

    @Override
    public void configureTasks(final ScheduledTaskRegistrar registrar) {
        registrar.setTaskScheduler(scheduler);
    }

}
