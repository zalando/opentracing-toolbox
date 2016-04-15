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
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.zalando.tracer.Trace;

import java.util.concurrent.atomic.AtomicReference;

@Component
public class BackgroundTask {

    private final AtomicReference<String> ref = new AtomicReference<>();
    
    @Autowired
    private Trace trace;
    
    @Scheduled(fixedDelay = 1)
    public void scheduledFixedDelay() {
        ref.set(trace.getValue());
    }

    public String getValue() {
        return ref.get();
    }

}
