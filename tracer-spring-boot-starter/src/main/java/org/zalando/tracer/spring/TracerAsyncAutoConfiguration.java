package org.zalando.tracer.spring;

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
