package org.zalando.tracer.spring;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.scheduling.annotation.SchedulingConfiguration;
import org.springframework.scheduling.annotation.SchedulingConfigurer;
import org.springframework.scheduling.concurrent.ConcurrentTaskScheduler;
import org.springframework.scheduling.config.ScheduledTaskRegistrar;
import org.zalando.tracer.Tracer;

import java.util.Optional;

@Configuration
@ConditionalOnClass(Scheduled.class)
@ConditionalOnProperty(name = "tracer.scheduling.enabled", havingValue = "true", matchIfMissing = true)
@AutoConfigureAfter({TracerAutoConfiguration.class, SchedulingConfiguration.class})
public class TracerSchedulingAutoConfiguration implements SchedulingConfigurer {

    @Autowired
    private Tracer tracer;

    @Override
    public void configureTasks(final ScheduledTaskRegistrar registrar) {
        final TaskScheduler scheduler = Optional.ofNullable(registrar.getScheduler())
                .orElseGet(ConcurrentTaskScheduler::new);
        registrar.setTaskScheduler(new TracerTaskScheduler(tracer, scheduler));
    }

}
