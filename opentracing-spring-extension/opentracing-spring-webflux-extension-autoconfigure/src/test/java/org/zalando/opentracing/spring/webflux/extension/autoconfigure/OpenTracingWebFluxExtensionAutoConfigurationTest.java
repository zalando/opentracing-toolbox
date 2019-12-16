package org.zalando.opentracing.spring.webflux.extension.autoconfigure;

import io.opentracing.mock.MockSpan;
import io.opentracing.mock.MockTracer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;
import java.util.Map;

import static com.google.common.collect.Iterables.getOnlyElement;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasEntry;
import static org.hamcrest.Matchers.hasKey;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.DEFINED_PORT;

@SpringBootTest(
        webEnvironment = DEFINED_PORT,
        properties = {
                "spring.main.web-application-type: reactive",
                "opentracing.spring.web.client.enabled: false"
        })
@EnableAutoConfiguration
class OpenTracingWebFluxExtensionAutoConfigurationTest {

    @Configuration
    @Import(ExampleController.class)
    static class TestConfiguration {

        @Bean
        public MockTracer tracer() {
            return new MockTracer();
        }

    }

    private WebClient client = WebClient.create("http://localhost:8080/");

    @Autowired
    private MockTracer tracer;

    @BeforeEach
    @AfterEach
    void setUp() {
        tracer.reset();
    }

    @Test
    void shouldTag() {
        final String body = client.post()
                .uri("/greetings/Alice")
                .retrieve()
                .bodyToMono(String.class)
                .block();

        assertThat(body, is("Hello, Alice!"));

        final Map<String, Object> tags = tags();

        assertThat(tags, hasEntry("component", "Spring WebFlux"));
        assertThat(tags, hasEntry("http.method", "POST"));
        assertThat(tags, hasEntry("http.path", "/greetings/{name}"));
        assertThat(tags, hasEntry("http.status_code", 200));
        assertThat(tags, hasKey("peer.address"));
        assertThat(tags, not(hasKey("http.hostname")));
        assertThat(tags, hasEntry("peer.ipv4", "127.0.0.1"));
        assertThat(tags, hasKey("peer.port"));
        assertThat(tags, hasEntry("span.kind", "server"));
    }

    private Map<String, Object> tags() {
        final MockSpan span = span();
        return span.tags();
    }

    private MockSpan span() {
        final List<MockSpan> spans = tracer.finishedSpans();
        assertThat(spans, hasSize(1));
        return getOnlyElement(spans);
    }

}
