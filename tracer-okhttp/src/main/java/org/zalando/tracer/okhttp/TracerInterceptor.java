package org.zalando.tracer.okhttp;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;
import org.apiguardian.api.API;
import org.zalando.tracer.Tracer;

import java.io.IOException;

import static org.apiguardian.api.API.Status.STABLE;

@API(status = STABLE)
public final class TracerInterceptor implements Interceptor {

    private final Tracer tracer;

    public TracerInterceptor(final Tracer tracer) {
        this.tracer = tracer;
    }

    @Override
    public Response intercept(final Chain chain) throws IOException {
        final Request.Builder builder = chain.request().newBuilder();
        tracer.forEach(builder::addHeader);
        return chain.proceed(builder.build());
    }

}
