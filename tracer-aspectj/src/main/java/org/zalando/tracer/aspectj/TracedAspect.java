package org.zalando.tracer.aspectj;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.zalando.tracer.Tracer;

@Aspect
public final class TracedAspect {

    private Tracer tracer;

    public void setTracer(final Tracer tracer) {
        this.tracer = tracer;
    }

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
