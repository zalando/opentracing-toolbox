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

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.servlet.DefaultServlet;
import org.eclipse.jetty.servlet.FilterHolder;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.junit.rules.TestWatcher;
import org.junit.runner.Description;
import org.zalando.tracer.servlet.example.AsyncServlet;
import org.zalando.tracer.servlet.example.BarServlet;
import org.zalando.tracer.servlet.example.FooServlet;
import org.zalando.tracer.servlet.example.ForwardServlet;
import org.zalando.tracer.servlet.example.IncludeServlet;

import javax.servlet.DispatcherType;
import javax.servlet.Filter;
import java.util.EnumSet;

public final class JettyRule extends TestWatcher {

    private final Server server;

    public JettyRule(final Filter filter) {
        this.server = new Server(0);

        final ServletContextHandler handler = new ServletContextHandler();
        handler.setContextPath("/");
        handler.addServlet(AsyncServlet.class, "/async");
        handler.addServlet(BarServlet.class, "/bar");
        handler.addServlet(FooServlet.class, "/foo");
        handler.addServlet(ForwardServlet.class, "/forward");
        handler.addServlet(IncludeServlet.class, "/include");
        handler.addServlet(DefaultServlet.class, "/");

        handler.addFilter(new FilterHolder(filter), "/async", EnumSet.allOf(DispatcherType.class));
        // /bar is intentionally NOT traced!
        handler.addFilter(new FilterHolder(filter), "/foo", EnumSet.allOf(DispatcherType.class));
        handler.addFilter(new FilterHolder(filter), "/forward", EnumSet.allOf(DispatcherType.class));
        handler.addFilter(new FilterHolder(filter), "/include", EnumSet.allOf(DispatcherType.class));

        server.setHandler(handler);
    }

    public int getPort() {
        final ServerConnector connector = (ServerConnector) server.getConnectors()[0];
        return connector.getLocalPort();
    }

    @Override
    protected void starting(Description description) {
        try {
            server.start();
        } catch (final Exception e) {
            throw new IllegalStateException(e);
        }
    }

    @Override
    protected void finished(Description description) {
        try {
            server.stop();
        } catch (final Exception e) {
            throw new IllegalStateException(e);
        }
    }

}
