package org.zalando.tracer.okhttp;

import com.github.restdriver.clientdriver.ClientDriverRule;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.zalando.tracer.Tracer;

import java.io.IOException;

import static com.github.restdriver.clientdriver.RestClientDriver.giveResponse;
import static com.github.restdriver.clientdriver.RestClientDriver.onRequestTo;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

public final class TracerInterceptorTest {

    @Rule
    public final ClientDriverRule driver = new ClientDriverRule();

    private final Tracer tracer = Tracer.builder()
            .trace("X-Trace-ID", () -> "16c38974-7530-11e5-bb35-10ddb1ee7671")
            .trace("X-Request-ID", () -> "2e7a3324-7530-11e5-ad30-10ddb1ee7671")
            .build();

    private final OkHttpClient client = new OkHttpClient.Builder()
            .addNetworkInterceptor(new TracerInterceptor(tracer))
            .build();

    @Before
    public void startTracer() {
        tracer.start();
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

        final Response response = client.newCall(new Request.Builder()
                .url(driver.getBaseUrl())
                .build()).execute();

        assertThat(response.code(), is(200));
        assertThat(response.body().string(), is("Hello, world!"));
    }

}
