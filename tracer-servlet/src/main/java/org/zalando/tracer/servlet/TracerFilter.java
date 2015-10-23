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

import org.zalando.tracer.Tracer;

import javax.servlet.DispatcherType;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Objects;
import java.util.function.Function;

public final class TracerFilter implements HttpFilter {

    private final Tracer tracer;

    public TracerFilter(final Tracer tracer) {
        this.tracer = tracer;
    }

    @Override
    public void doFilter(final HttpServletRequest request, final HttpServletResponse response, final FilterChain chain)
            throws ServletException, IOException {

        if (requiresTraceManagement(request.getDispatcherType())) {
            final Function<String, String> provider = selectProvider(request);

            tracer.start(provider);
            persistTraces(request, response);
            chain.doFilter(request, response);
            tracer.stop();
        } else {
            persistTraces(request, response);
            chain.doFilter(request, response);
        }
    }

    /**
     * Decided if trace management is required, i.e. whether {@link Tracer#start(Function)}
     * and {@link Tracer#stop()} need to be called or not. This method will return true for all
     * dispatcher types that are known to do their work in separate threads, i.e.
     * {@link DispatcherType#REQUEST REQUEST}, {@link DispatcherType#ASYNC ASYNC}
     * and {@link DispatcherType#ERROR ERROR}. {@link DispatcherType#FORWARD FORWARD} and
     * {@link DispatcherType#INCLUDE INCLUDE} on the other hand are performed on the original thread
     * and therefore don't require a new trace.
     *
     * @param dispatcherType the current request's dispatcher type
     * @return true if trace management is required, false otherwise
     */
    private boolean requiresTraceManagement(final DispatcherType dispatcherType) {
        switch (dispatcherType) {
            case FORWARD:
            case INCLUDE:
                return false;
            case REQUEST:
            case ASYNC:
            case ERROR:
            default:
                return true;
        }
    }

    private Function<String, String> selectProvider(final HttpServletRequest request) {
        final DispatcherType dispatcherType = request.getDispatcherType();
        switch (dispatcherType) {
            case FORWARD:
            case INCLUDE:
            case ASYNC:
            case ERROR:
                final Function<Object, String> toString = Objects::toString;
                final Function<String, Object> getAttribute = request::getAttribute;
                return getAttribute.andThen(toString);
            case REQUEST:
            default:
                return request::getHeader;
        }
    }

    private void persistTraces(final HttpServletRequest request, final HttpServletResponse response) {
        tracer.forEach(request::setAttribute);
        tracer.forEach(response::setHeader);
    }

}
