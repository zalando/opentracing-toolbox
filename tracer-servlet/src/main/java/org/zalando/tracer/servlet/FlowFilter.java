package org.zalando.tracer.servlet;

import org.apiguardian.api.API;
import org.zalando.tracer.Flow;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

import static org.apiguardian.api.API.Status.STABLE;

@API(status = STABLE)
public final class FlowFilter implements HttpFilter {

    private final Flow flow;

    public FlowFilter(final Flow flow) {
        this.flow = flow;
    }

    @Override
    public void doFilter(final HttpServletRequest request, final HttpServletResponse response,
            final FilterChain chain) throws IOException, ServletException {
        flow.readFrom(request::getHeader);
        if (isLegacyRequest(request)) {
            flow.writeTo(response::setHeader);
        }
        chain.doFilter(request, response);
    }

    private boolean isLegacyRequest(final HttpServletRequest request) {
        return request.getHeader(Flow.Header.FLOW_ID) != null;
    }

}
