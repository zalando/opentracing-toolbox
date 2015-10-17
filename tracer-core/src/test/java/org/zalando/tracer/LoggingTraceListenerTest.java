package org.zalando.tracer;

/*
 * ⁣​
 * Tracer
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

import org.junit.Test;
import org.slf4j.Logger;

import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.containsString;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.argThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

public final class LoggingTraceListenerTest {

    private final Logger logger = mock(Logger.class);
    private final Tracer tracer = Tracer.builder()
            .trace("X-Trace-ID", () -> "19c532c8-751f-11e5-a11d-10ddb1ee7671")
            .listener(new LoggingTraceListener(logger))
            .build();

    @Test
    public void shouldLogOnStart() throws Exception {
        tracer.start();

        verify(logger).trace(argThat(containsString("Started")),
                anyString(),
                argThat(containsString("19c532c8-751f-11e5-a11d-10ddb1ee7671")));
    }

    @Test
    public void shouldLogOnStop() throws Exception {
        tracer.start();
        tracer.stop();


        verify(logger).trace(argThat(containsString("Stopped")),
                anyString(),
                argThat(containsString("19c532c8-751f-11e5-a11d-10ddb1ee7671")));
    }

}