package org.zalando.tracer.servlet;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.servlet.DefaultServlet;
import org.eclipse.jetty.servlet.FilterHolder;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.junit.rules.TestWatcher;
import org.junit.runner.Description;
import org.zalando.tracer.Trace;
import org.zalando.tracer.servlet.example.AsyncServlet;
import org.zalando.tracer.servlet.example.FailingServlet;
import org.zalando.tracer.servlet.example.ForwardServlet;
import org.zalando.tracer.servlet.example.IncludeServlet;
import org.zalando.tracer.servlet.example.TraceServlet;

import javax.servlet.DispatcherType;
import javax.servlet.Filter;
import java.util.EnumSet;

public final class JettyRule extends TestWatcher {

    private final Server server;

    public JettyRule(final Filter filter, final Trace trace) {
        this.server = new Server(0);

        final ServletContextHandler handler = new ServletContextHandler();
        handler.setContextPath("/");
        handler.addServlet(new ServletHolder(new AsyncServlet(trace)), "/async");
        handler.addServlet(new ServletHolder(new TraceServlet(trace)), "/traced");
        handler.addServlet(new ServletHolder(new TraceServlet(trace)), "/untraced");
        handler.addServlet(ForwardServlet.class, "/forward");
        handler.addServlet(IncludeServlet.class, "/include");
        handler.addServlet(FailingServlet.class, "/failure");
        handler.addServlet(DefaultServlet.class, "/");

        handler.addFilter(new FilterHolder(filter), "/async", EnumSet.allOf(DispatcherType.class));
        // /untraced is intentionally NOT traced!
        handler.addFilter(new FilterHolder(filter), "/traced", EnumSet.allOf(DispatcherType.class));
        handler.addFilter(new FilterHolder(filter), "/forward", EnumSet.allOf(DispatcherType.class));
        handler.addFilter(new FilterHolder(filter), "/include", EnumSet.allOf(DispatcherType.class));
        handler.addFilter(new FilterHolder(filter), "/failure", EnumSet.allOf(DispatcherType.class));

        server.setHandler(handler);
    }

    public int getPort() {
        final ServerConnector connector = (ServerConnector) server.getConnectors()[0];
        return connector.getLocalPort();
    }

    @Override
    protected void starting(final Description description) {
        try {
            server.start();
        } catch (final Exception e) {
            throw new IllegalStateException(e);
        }
    }

    @Override
    protected void finished(final Description description) {
        try {
            server.stop();
        } catch (final Exception e) {
            throw new IllegalStateException(e);
        }
    }

}
