package org.zalando.opentracing.flowid.httpclient;

import org.apache.http.HttpRequest;
import org.apache.http.HttpRequestInterceptor;
import org.apache.http.protocol.HttpContext;
import org.apiguardian.api.API;
import org.zalando.opentracing.flowid.Flow;

import static org.apiguardian.api.API.Status.STABLE;

@API(status = STABLE)
public final class FlowHttpRequestInterceptor implements HttpRequestInterceptor {

    private final Flow flow;

    public FlowHttpRequestInterceptor(final Flow flow) {
        this.flow = flow;
    }

    @Override
    public void process(final HttpRequest request, final HttpContext context) {
        flow.writeTo(request::addHeader);
    }

}
