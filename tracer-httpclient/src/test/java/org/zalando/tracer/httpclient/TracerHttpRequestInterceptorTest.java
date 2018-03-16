package org.zalando.tracer.httpclient;

import com.github.restdriver.clientdriver.ClientDriver;
import com.github.restdriver.clientdriver.ClientDriverFactory;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.zalando.tracer.Tracer;

import java.io.IOException;

import static com.github.restdriver.clientdriver.RestClientDriver.giveResponse;
import static com.github.restdriver.clientdriver.RestClientDriver.onRequestTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

final class TracerHttpRequestInterceptorTest {

    private final ClientDriver server = new ClientDriverFactory().createClientDriver();

    private final Tracer tracer = Tracer.builder()
            .trace("X-Trace-ID", () -> "16c38974-7530-11e5-bb35-10ddb1ee7671")
            .trace("X-Request-ID", () -> "2e7a3324-7530-11e5-ad30-10ddb1ee7671")
            .build();

    private final CloseableHttpClient client = HttpClientBuilder.create()
            .addInterceptorFirst(new TracerHttpRequestInterceptor(tracer))
            .build();

    @BeforeEach
    void startTracer() {
        tracer.start();
    }

    @AfterEach
    void shutdownConnections() throws IOException {
        client.close();
    }

    @AfterEach
    void stopTracer() {
        tracer.stop();
    }

    @AfterEach
    void shutdownServer() {
        server.shutdownQuietly();
    }

    @Test
    void shouldAddHeader() throws IOException {
        server.addExpectation(onRequestTo("/")
                        .withHeader("X-Trace-ID", "16c38974-7530-11e5-bb35-10ddb1ee7671")
                        .withHeader("X-Request-ID", "2e7a3324-7530-11e5-ad30-10ddb1ee7671"),
                giveResponse("Hello, world!", "text/plain"));

        try (CloseableHttpResponse response = client.execute(new HttpGet(server.getBaseUrl()))) {
            assertThat(response.getStatusLine().getStatusCode(), is(200));
            assertThat(EntityUtils.toString(response.getEntity()), is("Hello, world!"));
        }

    }

}
