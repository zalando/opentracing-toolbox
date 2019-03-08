package org.zalando.tracer.httpclient;

import com.github.restdriver.clientdriver.ClientDriver;
import com.github.restdriver.clientdriver.ClientDriverFactory;
import io.opentracing.Scope;
import io.opentracing.Span;
import io.opentracing.mock.MockTracer;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.zalando.tracer.Flow;

import java.io.IOException;

import static com.github.restdriver.clientdriver.RestClientDriver.giveResponse;
import static com.github.restdriver.clientdriver.RestClientDriver.onRequestTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

final class FlowHttpRequestInterceptorTest {

    private final ClientDriver server = new ClientDriverFactory().createClientDriver();

    private final MockTracer tracer = new MockTracer();
    private final Flow flow = Flow.create(tracer);

    private final CloseableHttpClient client = HttpClientBuilder.create()
            .addInterceptorFirst(new FlowHttpRequestInterceptor(flow))
            .build();

    @AfterEach
    void shutdownConnections() throws IOException {
        client.close();
    }

    @AfterEach
    void verify() {
        server.verify();
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
            try (final CloseableHttpResponse response = client.execute(new HttpGet(server.getBaseUrl()))) {
                assertThat(response.getStatusLine().getStatusCode(), is(200));
                assertThat(EntityUtils.toString(response.getEntity()), is("Hello, world!"));
            }
        }
    }

}
