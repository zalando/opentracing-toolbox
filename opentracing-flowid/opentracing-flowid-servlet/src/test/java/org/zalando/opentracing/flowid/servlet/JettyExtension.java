package org.zalando.opentracing.flowid.servlet;

import io.opentracing.Tracer;
import io.opentracing.contrib.web.servlet.filter.TracingFilter;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.servlet.FilterHolder;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.junit.jupiter.api.extension.AfterEachCallback;
import org.junit.jupiter.api.extension.BeforeEachCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.zalando.opentracing.flowid.Flow;
import org.zalando.opentracing.flowid.servlet.example.TraceServlet;

import javax.servlet.DispatcherType;
import javax.servlet.Filter;
import java.util.EnumSet;

final class JettyExtension implements BeforeEachCallback, AfterEachCallback {

    private final Server server = new Server(0);

    JettyExtension(final Filter filter, final Tracer tracer, final Flow flow) {
        final ServletContextHandler handler = new ServletContextHandler();
        handler.setContextPath("/");
        handler.addServlet(new ServletHolder(new TraceServlet(flow)), "/trace");

        handler.addFilter(new FilterHolder(new TracingFilter(tracer)), "/trace", EnumSet.allOf(DispatcherType.class));

        // /untraced is intentionally NOT traced!
        handler.addFilter(new FilterHolder(filter), "/trace", EnumSet.allOf(DispatcherType.class));

        server.setHandler(handler);
    }

    public int getPort() {
        final ServerConnector connector = (ServerConnector) server.getConnectors()[0];
        return connector.getLocalPort();
    }

    @Override
    public void beforeEach(final ExtensionContext context) throws Exception {
        server.start();
    }

    @Override
    public void afterEach(final ExtensionContext context) throws Exception {
        server.stop();
    }

}
