package org.zalando.tracer.aspectj;

/*
 * ⁣​
 * Tracer: AspectJ
 * ⁣⁣
 * Copyright (C) 2015 Zalando SE
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
