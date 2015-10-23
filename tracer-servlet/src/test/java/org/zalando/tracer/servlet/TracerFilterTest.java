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

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.RequestBuilder;
import org.zalando.tracer.Generator;

import javax.servlet.DispatcherType;

import static org.hamcrest.Matchers.notNullValue;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.http.HttpMethod.GET;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.asyncDispatch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.request;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;

// TODO verify async support
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = TestConfiguration.class)
@WebAppConfiguration
public final class TracerFilterTest {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private Generator uuidGenerator;

    @Autowired
    private Generator flowIDGenerator;

    @Before
    public void resetMocks() {
        reset(uuidGenerator, flowIDGenerator);
    }

    @Test
    public void shouldAddResponseHeadersWhenTraceIsActive() throws Exception {
        mvc.perform(request(GET, "/traced"))
                .andExpect(header().string("X-Trace-ID", notNullValue()))
                .andExpect(header().string("X-Flow-ID", notNullValue()));
    }

    @Test
    public void shouldNotAddResponseHeadersWhenTraceIsNotActive() throws Exception {
        mvc.perform(request(GET, "/not-traced"))
                .andExpect(header().doesNotExist("X-Trace-ID"))
                .andExpect(header().doesNotExist("X-Flow-ID"));
    }

    @Test
    public void shouldManageTraceForAsyncDispatch() throws Exception {
        final MvcResult result = mvc.perform(request(GET, "/traced-async"))
                .andReturn();

        mvc.perform(async(result))
                .andExpect(header().string("X-Trace-ID", notNullValue()))
                .andExpect(header().string("X-Flow-ID", notNullValue()));

        verify(uuidGenerator, times(1)).generate();
        verify(flowIDGenerator, times(1)).generate();
    }

    private RequestBuilder async(final MvcResult result) {
        final RequestBuilder builder = asyncDispatch(result);
        return context -> {
            final MockHttpServletRequest request = builder.buildRequest(context);
            request.setDispatcherType(DispatcherType.ASYNC);
            return request;
        };
    }

    @Test
    public void shouldNotManageTraceForForwardDispatch() throws Exception {
        final MvcResult result = mvc.perform(request(GET, "/traced-forward"))
                .andReturn();

        mvc.perform(forward(result))
                .andExpect(header().string("X-Trace-ID", notNullValue()))
                .andExpect(header().string("X-Flow-ID", notNullValue()));

        verify(uuidGenerator, times(1)).generate();
        verify(flowIDGenerator, times(1)).generate();
    }

    // https://github.com/spring-projects/spring-mvc-showcase/issues/42
    private static RequestBuilder forward(final MvcResult result) {
        return context -> {
            final MockHttpServletRequest request = result.getRequest();
            request.setRequestURI(result.getResponse().getForwardedUrl());
            request.setDispatcherType(DispatcherType.FORWARD);
            return request;
        };
    }

    // TODO include

    private static RequestBuilder include(final MvcResult result) {
        return context -> {
            final MockHttpServletRequest request = result.getRequest();
            request.setRequestURI(result.getResponse().getIncludedUrl());
            request.setDispatcherType(DispatcherType.INCLUDE);
            return request;
        };
    }

}