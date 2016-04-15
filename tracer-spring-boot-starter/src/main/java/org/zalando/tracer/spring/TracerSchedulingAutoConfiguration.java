package org.zalando.tracer.spring;

/*
 * ⁣​
 * Tracer: Spring Boot Starter
 * ⁣⁣
 * Copyright (C) 2015 - 2016 Zalando SE
 * ⁣⁣
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * ​⁣
 */

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
