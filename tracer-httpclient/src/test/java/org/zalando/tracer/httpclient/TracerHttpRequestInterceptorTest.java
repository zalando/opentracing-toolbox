package org.zalando.tracer.httpclient;

import com.github.restdriver.clientdriver.ClientDriverRule;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.zalando.tracer.Tracer;

import java.io.DataInputStream;
import java.io.IOException;

import static com.github.restdriver.clientdriver.RestClientDriver.giveResponse;
import static com.github.restdriver.clientdriver.RestClientDriver.onRequestTo;
import static java.nio.charset.StandardCharsets.UTF_8;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

public final class TracerHttpRequestInterceptorTest {

    @Rule
    public final ClientDriverRule driver = new ClientDriverRule();

    private final Tracer tracer = Tracer.builder()
            .trace("X-Trace-ID", () -> "16c38974-7530-11e5-bb35-10ddb1ee7671")
            .trace("X-Request-ID", () -> "2e7a3324-7530-11e5-ad30-10ddb1ee7671")
            .build();
    private final CloseableHttpClient client = HttpClientBuilder.create()
            .addInterceptorFirst(new TracerHttpRequestInterceptor(tracer))
            .build();


    @Before
    public void startTracer() {
        tracer.start();
    }

    @After
    public void shutdownConnections() throws IOException {
        client.close();
    }

    @After
    public void stopTracer() {
        tracer.stop();
    }

    @Test
    public void shouldAddHeader() throws IOException {
        driver.addExpectation(onRequestTo("/")
                        .withHeader("X-Trace-ID", "16c38974-7530-11e5-bb35-10ddb1ee7671")
                        .withHeader("X-Request-ID", "2e7a3324-7530-11e5-ad30-10ddb1ee7671"),
                giveResponse("Hello, world!", "text/plain"));

        try (CloseableHttpResponse response = client.execute(new HttpGet(driver.getBaseUrl()))) {
            assertThat(response.getStatusLine().getStatusCode(), is(200));
            final byte[] bytes = new byte[(int) response.getEntity().getContentLength()];
            new DataInputStream(response.getEntity().getContent()).readFully(bytes);
            assertThat(new String(bytes, UTF_8), is("Hello, world!"));
        }

    }

}