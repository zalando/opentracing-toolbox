package org.zalando.tracer.servlet;

/*
 * ⁣​
 * Tracer: Servlet
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

import org.junit.Rule;
import org.junit.Test;
import org.zalando.tracer.Trace;
import org.zalando.tracer.Tracer;

import static com.jayway.restassured.RestAssured.given;
import static java.lang.String.format;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

public final class TracerFilterLifecycleTest {

    private final Tracer tracer = mock(Tracer.class);

    @Rule
    public final JettyRule jetty = new JettyRule(new TracerFilter(tracer), mock(Trace.class));

    private String url(final String path) {
        return format("http://localhost:%d%s", jetty.getPort(), path);
    }

    @Test
    public void shouldStartAndStopTracer() throws Exception {
        given().
                when()
                .get(url("/traced"));

        verify(tracer, times(1)).start(any());
        verify(tracer, times(1)).stop();
    }

    @Test
    public void shouldStartAndStopTracerOnFailure() throws Exception {
        given().
                when()
                .get(url("/failure"));

        verify(tracer, times(1)).start(any());
        verify(tracer, times(1)).stop();
    }

    @Test
    public void shouldIgnoreTracerWhenTraceIsNotActive() throws Exception {
        given().
                when()
                .get(url("/untraced"));

        verifyNoMoreInteractions(tracer);
    }

}