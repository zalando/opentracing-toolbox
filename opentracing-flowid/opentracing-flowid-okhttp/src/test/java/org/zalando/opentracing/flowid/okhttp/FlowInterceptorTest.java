package org.zalando.opentracing.flowid.okhttp;

import com.github.restdriver.clientdriver.ClientDriver;
import com.github.restdriver.clientdriver.ClientDriverFactory;
import io.opentracing.Scope;
import io.opentracing.Span;
import io.opentracing.mock.MockTracer;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.zalando.opentracing.flowid.Flow;

import java.io.IOException;

import static com.github.restdriver.clientdriver.RestClientDriver.giveResponse;
import static com.github.restdriver.clientdriver.RestClientDriver.onRequestTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

final class FlowInterceptorTest {

    private final ClientDriver server = new ClientDriverFactory().createClientDriver();

    private final MockTracer tracer = new MockTracer();
    private final Flow flow = Flow.create(tracer);

    private final OkHttpClient client = new OkHttpClient.Builder()
            .addNetworkInterceptor(new FlowInterceptor(flow))
            .build();

    @AfterEach
    void shutdownServer() {
        server.shutdownQuietly();
    }

    @Test
    void shouldAddHeaderFromTraceId() throws IOException {
        final Span span = tracer.buildSpan("test").start();

        server.addExpectation(onRequestTo("/")
                        .withHeader("X-Flow-ID", span.context().toTraceId()),
                giveResponse("Hello, world!", "text/plain"));

        execute(span);
    }

    @Test
    void shouldAddHeaderFromBaggage() throws IOException {
        server.addExpectation(onRequestTo("/")
                        .withHeader("X-Flow-ID", "REcCvlqMSReeo7adheiYFA"),
                giveResponse("Hello, world!", "text/plain"));

        execute(tracer.buildSpan("test").start()
                .setBaggageItem("flow_id", "REcCvlqMSReeo7adheiYFA"));
    }

    private void execute(final Span span) throws IOException {
        try (final Scope ignored = tracer.activateSpan(span)) {
            final Response response = client.newCall(new Request.Builder()
                    .url(server.getBaseUrl())
                    .build()).execute();

            assertThat(response.code(), is(200));
            assertThat(response.body().string(), is("Hello, world!"));
        }
    }

}
