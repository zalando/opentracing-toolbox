package org.zalando.tracer.httpclient;

import org.apache.http.HttpRequest;
import org.apache.http.HttpRequestInterceptor;
import org.apache.http.protocol.HttpContext;
import org.apiguardian.api.API;
import org.zalando.tracer.Tracer;

import static org.apiguardian.api.API.Status.STABLE;

@API(status = STABLE)
public final class TracerHttpRequestInterceptor implements HttpRequestInterceptor {

    private final Tracer tracer;

    public TracerHttpRequestInterceptor(final Tracer tracer) {
        this.tracer = tracer;
    }

    @Override
    public void process(final HttpRequest request, final HttpContext context) {
        tracer.forEach(request::addHeader);
    }

}
