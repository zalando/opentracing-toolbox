package org.zalando.opentracing.spring.webflux.extension;

import io.opentracing.contrib.spring.web.webfilter.WebFluxSpanDecorator;
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
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;

import java.util.List;
import java.util.Map;
import java.util.Objects;

import static com.google.common.collect.Iterables.getOnlyElement;
import static java.util.Collections.singletonMap;
import static java.util.regex.Pattern.compile;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasEntry;
import static org.hamcrest.Matchers.hasKey;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.matchesPattern;
import static org.hamcrest.Matchers.not;
import static org.hobsoft.hamcrest.compose.ComposeMatchers.hasFeature;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.DEFINED_PORT;
import static org.springframework.http.HttpStatus.SERVICE_UNAVAILABLE;

@SpringBootTest(
        webEnvironment = DEFINED_PORT,
        properties = "opentracing.spring.web.client.enabled: false")
@EnableAutoConfiguration
class StandardSpanDecoratorTest {

    @Configuration
    @Import(ExampleController.class)
    static class TestConfiguration {

        @Bean
        public MockTracer tracer() {
            return new MockTracer();
        }

        @Bean
        public WebFluxSpanDecorator standardSpanDecorator() {
            return new StandardSpanDecorator();
        }

        @Bean
        public WebFluxSpanDecorator httpUrlSpanDecorator() {
            return new HttpUrlSpanDecorator();
        }

        @Bean
        public WebFluxSpanDecorator staticSpanDecorator() {
            return new StaticSpanDecorator(singletonMap("test", "true"));
        }

        @Bean
        public RouterFunction<ServerResponse> routerFunction() {
            return RouterFunctions.route()
                    .GET("/exception", request -> {
                        throw new UnsupportedOperationException("Error");
                    })
                    .build();
        }

    }

    private final WebClient client = WebClient.create("http://localhost:8080/");

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
                .header("HTTP-Method-Override", "GET")
                .header("Prefer", "handling=strict")
                .retrieve()
                .bodyToMono(String.class)
                .block();

        assertThat(body, is("Hello, Alice!"));

        final Map<String, Object> tags = tags();

        assertThat(tags, hasEntry("component", "Spring WebFlux"));
        assertThat(tags, hasEntry("http.method", "POST"));
        assertThat(tags, hasEntry("http.method_override", "GET"));
        assertThat(tags, hasEntry("http.path", "/greetings/{name}"));
        assertThat(tags, hasEntry("http.prefer", "handling=strict"));
        assertThat(tags, hasEntry("http.retry_after", "60"));
        assertThat(tags, hasEntry("http.status_code", 200));
        assertThat(tags, hasEntry("http.url", "http://localhost:8080/greetings/Alice"));
        assertThat(tags, hasEntry(equalTo("peer.address"),
                hasFeature(Object::toString, matchesPattern(compile("127.0.0.1:\\d+")))));
        assertThat(tags, not(hasKey("http.hostname")));
        assertThat(tags, hasEntry("peer.ipv4", "127.0.0.1"));
        assertThat(tags, hasEntry(equalTo("peer.port"),
                hasFeature(Object::toString, matchesPattern(compile("\\d+")))));
        assertThat(tags, hasEntry("span.kind", "server"));
        assertThat(tags, hasEntry("test", "true"));
    }

    @Test
    void shouldTag5xxErrorResponse() {
        final ClientResponse response = client.get()
                .uri("/error")
                .exchange()
                .block();

        assertThat(response.statusCode(), is(SERVICE_UNAVAILABLE));

        final Map<String, Object> tags = tags();

        assertThat(tags, hasEntry("http.path", "/error"));
        assertThat(tags, hasEntry("error", true));
    }

    @Test
    void shouldTagException() {
        final ClientResponse response = client.get()
                .uri("/exception")
                .exchange()
                .block();

        assertTrue(response.statusCode().is5xxServerError());

        final MockSpan span = span();
        final Map<String, Object> tags = span.tags();

        assertThat(tags, hasEntry("http.path", "/exception"));
        assertThat(tags, hasEntry("error", true));

        final List<MockSpan.LogEntry> logs = span.logEntries();
        assertThat(logs, hasSize(3));

        assertThat(logs.get(0).fields(), hasEntry(equalTo("message"),
                hasFeature(Objects::toString, containsString("Error"))));
        assertThat(logs.get(1).fields(),
                hasEntry("error.kind", "UnsupportedOperationException"));
        assertThat(logs.get(1).fields().get("error.object"),
                instanceOf(UnsupportedOperationException.class));
        assertThat(logs.get(2).fields(),
                hasEntry(equalTo("stack"), instanceOf(String.class)));
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
