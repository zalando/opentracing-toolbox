package org.zalando.opentracing.flowid.servlet.example;

import java.io.IOException;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.zalando.opentracing.flowid.Flow;

public final class TraceServlet extends HttpServlet {

    private final Flow flow;

    public TraceServlet(final Flow flow) {
        this.flow = flow;
    }

    @Override
    protected void doGet(final HttpServletRequest request, final HttpServletResponse response) throws IOException {
        response.getWriter().print(flow.currentSpanId().orElse(null));
    }

}
