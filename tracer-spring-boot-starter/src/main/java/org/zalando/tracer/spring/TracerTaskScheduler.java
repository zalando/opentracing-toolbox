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

import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.Trigger;
import org.zalando.tracer.Tracer;

import java.util.Date;
import java.util.concurrent.ScheduledFuture;

final class TracerTaskScheduler implements TaskScheduler {

    private final Tracer tracer;
    private final TaskScheduler scheduler;

    TracerTaskScheduler(final Tracer tracer, final TaskScheduler scheduler) {
        this.tracer = tracer;
        this.scheduler = scheduler;
    }

    @Override
    public ScheduledFuture<?> schedule(final Runnable runnable, final Trigger trigger) {
        return scheduler.schedule(tracer.manage(runnable), trigger);
    }

    @Override
    public ScheduledFuture<?> schedule(final Runnable runnable, final Date date) {
        return scheduler.schedule(tracer.manage(runnable), date);
    }

    @Override
    public ScheduledFuture<?> scheduleAtFixedRate(final Runnable runnable, final Date date, final long period) {
        return scheduler.scheduleAtFixedRate(tracer.manage(runnable), date, period);
    }

    @Override
    public ScheduledFuture<?> scheduleAtFixedRate(final Runnable runnable, final long period) {
        return scheduler.scheduleAtFixedRate(tracer.manage(runnable), period);
    }

    @Override
    public ScheduledFuture<?> scheduleWithFixedDelay(final Runnable runnable, final Date date, final long delay) {
        return scheduler.scheduleWithFixedDelay(tracer.manage(runnable), date, delay);
    }

    @Override
    public ScheduledFuture<?> scheduleWithFixedDelay(final Runnable runnable, final long delay) {
        return scheduler.scheduleWithFixedDelay(tracer.manage(runnable), delay);
    }

}
