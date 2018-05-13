package org.zalando.tracer.servlet.example;

import org.zalando.tracer.Trace;

import javax.servlet.AsyncContext;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public final class AsyncServlet extends HttpServlet {

    private final ExecutorService executor = Executors.newSingleThreadExecutor();

    private final Trace trace;

    public AsyncServlet(final Trace trace) {
        this.trace = trace;
    }
    
    @Override
    protected void doGet(final HttpServletRequest request, final HttpServletResponse response) {
        final AsyncContext context = request.startAsync(request, response);
        context.setTimeout(100);

        executor.submit(() -> {
            context.getResponse().getWriter().println(trace.getValue());
            context.complete();
            return null;
        });
    }

}
