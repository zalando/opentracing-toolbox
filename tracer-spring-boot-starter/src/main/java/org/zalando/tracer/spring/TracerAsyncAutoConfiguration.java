package org.zalando.tracer.spring;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.concurrent.ConcurrentTaskExecutor;
import org.zalando.tracer.Tracer;

import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;

import static java.util.concurrent.Executors.newCachedThreadPool;
import static org.springframework.aop.interceptor.AsyncExecutionAspectSupport.DEFAULT_TASK_EXECUTOR_BEAN_NAME;
import static org.zalando.tracer.concurrent.TracingExecutors.preserve;
import static org.zalando.tracer.concurrent.TracingExecutors.tryPreserve;

@Configuration
@ConditionalOnClass(Async.class)
@ConditionalOnProperty(name = "tracer.async.enabled", havingValue = "true", matchIfMissing = true)
@AutoConfigureAfter(TracerAutoConfiguration.class)
public class TracerAsyncAutoConfiguration {

    @Bean(destroyMethod = "shutdown")
    @ConditionalOnMissingBean(name = DEFAULT_TASK_EXECUTOR_BEAN_NAME)
    public ExecutorService taskExecutor() {
        return newCachedThreadPool();
    }

    @Bean
    @Primary
    @ConditionalOnBean(name = DEFAULT_TASK_EXECUTOR_BEAN_NAME)
    public TaskExecutor preservingTaskExecutor(@Qualifier(DEFAULT_TASK_EXECUTOR_BEAN_NAME) final Executor executor,
            final Tracer tracer) {
        return new ConcurrentTaskExecutor(tryPreserve(executor, tracer));
    }

}
