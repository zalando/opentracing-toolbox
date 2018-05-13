package org.zalando.tracer.servlet.example;

import org.zalando.tracer.Trace;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public final class TraceServlet extends HttpServlet {

    private final Trace trace;

    public TraceServlet(final Trace trace) {
        this.trace = trace;
    }

    @Override
    protected void doGet(final HttpServletRequest request, final HttpServletResponse response) throws IOException {
        response.getWriter().println(trace.getValue());
    }

}
