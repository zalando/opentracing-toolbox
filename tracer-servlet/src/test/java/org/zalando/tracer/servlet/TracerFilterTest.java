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

import com.jayway.restassured.RestAssured;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.zalando.tracer.FlowIDGenerator;
import org.zalando.tracer.Generator;
import org.zalando.tracer.Tracer;
import org.zalando.tracer.UUIDGenerator;

import static com.jayway.restassured.RestAssured.given;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.nullValue;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

public final class TracerFilterTest {

    @Rule
    public final JettyRule jetty;

    private final Generator uuidGenerator = spy(new MockGenerator(new UUIDGenerator()));
    private final Generator flowIDGenerator = spy(new MockGenerator(new FlowIDGenerator()));

    public TracerFilterTest() {
        this.jetty = new JettyRule(new TracerFilter(Tracer.builder()
                .trace("X-Trace-ID", uuidGenerator)
                .trace("X-Flow-ID", flowIDGenerator)
                .build()));
    }

    @Before
    public void setPort() {
        RestAssured.port = jetty.getPort();
    }

    @Before
    public void resetMocks() {
        reset(uuidGenerator, flowIDGenerator);
    }

    @Test
    public void shouldAddResponseHeadersWhenTraceIsActive() throws Exception {
        given().
                when().
                get("/foo").
                then()
                .header("X-Trace-ID", notNullValue())
                .header("X-Flow-ID", notNullValue());
    }

    @Test
    public void shouldNotAddResponseHeadersWhenTraceIsNotActive() throws Exception {
        given().
                when().
                get("/bar").
                then()
                .header("X-Trace-ID", nullValue())
                .header("X-Flow-ID", nullValue());
    }

    @Test
    public void shouldManageTraceForAsyncDispatch() throws Exception {
        given().
                when().
                get("/async");

        verify(uuidGenerator, times(1)).generate();
        verify(flowIDGenerator, times(1)).generate();
    }

    @Test
    public void shouldNotManageTraceForAsyncDispatch() throws Exception {
        given().
                when().
                get("/async");

        verify(uuidGenerator, times(1)).generate();
        verify(flowIDGenerator, times(1)).generate();
    }

    @Test
    public void shouldNotManageTraceForForwardDispatch() throws Exception {
        given().
                when().
                get("/forward");

        verify(uuidGenerator, times(1)).generate();
        verify(flowIDGenerator, times(1)).generate();
    }

    @Test
    public void shouldNotManageTraceForIncludeDispatch() throws Exception {
        given().
                when().
                get("/include");

        verify(uuidGenerator, times(1)).generate();
        verify(flowIDGenerator, times(1)).generate();
    }

}