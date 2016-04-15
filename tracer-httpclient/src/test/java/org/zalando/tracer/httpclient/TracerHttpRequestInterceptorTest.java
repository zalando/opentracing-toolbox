package org.zalando.tracer.httpclient;

/*
 * ⁣​
 * Tracer: HttpClient
 * ⁣⁣
 * Copyright (C) 2015 Zalando SE
 * ⁣⁣
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * ​⁣
 */

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

import java.io.IOException;

import static com.github.restdriver.clientdriver.RestClientDriver.giveResponse;
import static com.github.restdriver.clientdriver.RestClientDriver.onRequestTo;
import static com.google.common.io.ByteStreams.toByteArray;
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
            assertThat(new String(toByteArray(response.getEntity().getContent()), UTF_8), is("Hello, world!"));
        }

    }

}