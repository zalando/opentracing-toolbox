package org.zalando.tracer.httpclient;

import org.apache.http.HttpRequest;
import org.apache.http.HttpRequestInterceptor;
import org.apache.http.protocol.HttpContext;
import org.zalando.tracer.Tracer;

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
