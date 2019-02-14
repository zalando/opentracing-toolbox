package org.zalando.tracer.servlet.example;

import org.zalando.tracer.Flow;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public final class TraceServlet extends HttpServlet {

    private final Flow flow;

    public TraceServlet(final Flow flow) {
        this.flow = flow;
    }

    @Override
    protected void doGet(final HttpServletRequest request, final HttpServletResponse response) throws IOException {
        response.getWriter().print(flow.currentId());
    }

}
