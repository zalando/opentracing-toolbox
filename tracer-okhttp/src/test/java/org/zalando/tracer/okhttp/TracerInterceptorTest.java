package org.zalando.tracer.okhttp;

import com.github.restdriver.clientdriver.ClientDriver;
import com.github.restdriver.clientdriver.ClientDriverFactory;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.zalando.tracer.Tracer;

import java.io.IOException;

import static com.github.restdriver.clientdriver.RestClientDriver.giveResponse;
import static com.github.restdriver.clientdriver.RestClientDriver.onRequestTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

final class TracerInterceptorTest {

    private final ClientDriver server = new ClientDriverFactory().createClientDriver();

    private final Tracer tracer = Tracer.builder()
            .trace("X-Trace-ID", () -> "16c38974-7530-11e5-bb35-10ddb1ee7671")
            .trace("X-Request-ID", () -> "2e7a3324-7530-11e5-ad30-10ddb1ee7671")
            .build();

    private final OkHttpClient client = new OkHttpClient.Builder()
            .addNetworkInterceptor(new TracerInterceptor(tracer))
            .build();

    @BeforeEach
    void startTracer() {
        tracer.start();
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

        final Response response = client.newCall(new Request.Builder()
                .url(server.getBaseUrl())
                .build()).execute();

        assertThat(response.code(), is(200));
        assertThat(response.body().string(), is("Hello, world!"));
    }

}
