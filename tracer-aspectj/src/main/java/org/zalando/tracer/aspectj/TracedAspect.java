package org.zalando.tracer.aspectj;

import org.apiguardian.api.API;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.zalando.tracer.Tracer;

import static org.apiguardian.api.API.Status.INTERNAL;
import static org.apiguardian.api.API.Status.STABLE;

@API(status = STABLE)
@Aspect
public final class TracedAspect {

    private Tracer tracer;

    @API(status = INTERNAL)
    public void setTracer(final Tracer tracer) {
        this.tracer = tracer;
    }

    @API(status = INTERNAL)
    @Around("execution(@Traced * *(..))")
    public final Object advice(final ProceedingJoinPoint joinPoint) throws Throwable {
        tracer.start();

        try {
            return joinPoint.proceed();
        } finally {
            tracer.stop();
        }
    }

}
