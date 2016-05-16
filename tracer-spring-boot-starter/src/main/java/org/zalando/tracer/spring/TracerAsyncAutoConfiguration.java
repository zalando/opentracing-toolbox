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
import org.springframework.core.task.AsyncTaskExecutor;
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.AsyncConfigurerSupport;
import org.springframework.scheduling.concurrent.ConcurrentTaskExecutor;
import org.zalando.tracer.Tracer;

import java.util.Optional;
import java.util.concurrent.Executor;

@Configuration
@ConditionalOnClass(Async.class)
@ConditionalOnProperty(name = "tracer.async.enabled", havingValue = "true", matchIfMissing = true)
@AutoConfigureAfter(TracerAutoConfiguration.class)
public class TracerAsyncAutoConfiguration extends AsyncConfigurerSupport {

    @Autowired
    private Tracer tracer;

    @Autowired(required = false)
    private AsyncTaskExecutor executor;

    @Override
    public Executor getAsyncExecutor() {
        final TaskExecutor delegate = Optional.ofNullable(executor).orElseGet(ConcurrentTaskExecutor::new);
        return new TracerTaskExecutor(tracer, delegate);
    }

}
