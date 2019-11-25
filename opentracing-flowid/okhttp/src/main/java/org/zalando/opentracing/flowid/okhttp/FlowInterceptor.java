package org.zalando.opentracing.flowid.okhttp;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;
import org.apiguardian.api.API;
import org.zalando.opentracing.flowid.Flow;

import java.io.IOException;

import static org.apiguardian.api.API.Status.STABLE;

@API(status = STABLE)
public final class FlowInterceptor implements Interceptor {

    private final Flow flow;

    public FlowInterceptor(final Flow flow) {
        this.flow = flow;
    }

    @Override
    public Response intercept(final Chain chain) throws IOException {
        final Request.Builder builder = chain.request().newBuilder();
        flow.writeTo(builder::addHeader);
        return chain.proceed(builder.build());
    }

}
