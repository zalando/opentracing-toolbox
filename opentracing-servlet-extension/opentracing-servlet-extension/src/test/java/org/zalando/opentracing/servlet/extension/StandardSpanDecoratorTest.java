package org.zalando.opentracing.servlet.extension;

import com.google.common.base.Throwables;
import io.opentracing.Tracer;
import io.opentracing.contrib.web.servlet.filter.ServletFilterSpanDecorator;
import io.opentracing.contrib.web.servlet.filter.TracingFilter;
import io.opentracing.mock.MockSpan;
import io.opentracing.mock.MockTracer;
import lombok.SneakyThrows;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.web.client.DefaultResponseErrorHandler;
import org.springframework.web.client.RestTemplate;

import java.time.Duration;
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
import static org.springframework.http.HttpMethod.POST;
import static org.springframework.http.HttpStatus.OK;
import static org.springframework.http.HttpStatus.SERVICE_UNAVAILABLE;

@ExtendWith(SpringExtension.class) // only needed for Spring 4 support
@SpringBootTest(
        webEnvironment = DEFINED_PORT,
        properties = {
                "opentracing.spring.web.client.enabled: false",
                "spring.mvc.async.request-timeout: 1000"
        })
@EnableAutoConfiguration(excludeName = {
        "org.springframework.boot.autoconfigure.web.ErrorMvcAutoConfiguration",
        "org.springframework.boot.autoconfigure.web.servlet.error.ErrorMvcAutoConfiguration",
})
class StandardSpanDecoratorTest {

    @Configuration
    @Import(ExampleController.class)
    static class TestConfiguration {

        @Bean
        public MockTracer tracer() {
            return new MockTracer();
        }

        @Bean
        @SuppressWarnings({"rawtypes", "unchecked"}) // generic as of Spring Boot 2
        public FilterRegistrationBean tracingFilter(
                final Tracer tracer,
                final List<ServletFilterSpanDecorator> decorators) {

            final TracingFilter filter = new TracingFilter(tracer, decorators, null);
            final FilterRegistrationBean bean = new FilterRegistrationBean(filter);
            bean.setAsyncSupported(true);

            return bean;
        }

        @Bean
        public ServletFilterSpanDecorator standardSpanDecorator() {
            return new StandardSpanDecorator();
        }

        @Bean
        public ServletFilterSpanDecorator httpUrlSpanDecorator() {
            return new HttpUrlSpanDecorator();
        }

        @Bean
        public ServletFilterSpanDecorator staticSpanDecorator() {
            return new StaticSpanDecorator(singletonMap("test", "true"));
        }

    }

    private final RestTemplate client = new RestTemplate();

    public StandardSpanDecoratorTest() {
        this.client.setErrorHandler(new DefaultResponseErrorHandler() {
            @Override
            public void handleError(final ClientHttpResponse response) {
                // don't throw
            }
        });
    }

    @Autowired
    private MockTracer tracer;

    @BeforeEach
    @AfterEach
    void reset() {
        tracer.reset();
    }

    @Test
    void shouldTag() {
        final HttpHeaders headers = new HttpHeaders();
        headers.set("HTTP-Method-Override", "GET");
        headers.set("Prefer", "handling=strict");
        final HttpEntity<Object> entity = new HttpEntity<>(headers);

        final ResponseEntity<String> response = client
                .exchange("http://localhost:8080/greet?name=Alice", POST, entity, String.class);

        assertThat(response.getStatusCode(), is(OK));
        assertThat(response.getBody(), is("Hello, Alice!"));

        waitFor(Duration.ofSeconds(1));

        final Map<String, Object> tags = tags();

        assertThat(tags, hasEntry("component", "Servlet"));
        assertThat(tags, hasEntry("http.method", "POST"));
        assertThat(tags, hasEntry("http.method_override", "GET"));
        assertThat(tags, not(hasKey("http.path")));
        assertThat(tags, hasEntry("http.prefer", "handling=strict"));
        assertThat(tags, hasEntry("http.retry_after", "60"));
        assertThat(tags, hasEntry("http.status_code", 200));
        assertThat(tags, hasEntry("http.url", "http://localhost:8080/greet?name=Alice"));
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
    void shouldNotTagHandledTimeoutAsError() {
        final String response = client
                .getForObject("http://localhost:8080/async", String.class);

        assertThat(response, is("Interrupted"));

        waitFor(Duration.ofSeconds(1));

        final MockSpan span = span();
        final Map<String, Object> tags = span.tags();

        assertThat(tags, not(hasKey("error")));
    }

    @Test
    void shouldTag5xxErrorResponse() {
        final ResponseEntity<String> response = client
                .getForEntity("http://localhost:8080/error", String.class);
        assertThat(response.getStatusCode(), is(SERVICE_UNAVAILABLE));

        assertThat(tags(), hasEntry("error", true));
    }

    @Test
    void shouldTagException() {
        final ResponseEntity<String> response = client
                .getForEntity("http://localhost:8080/exception", String.class);

        assertTrue(response.getStatusCode().is5xxServerError());

        final MockSpan span = span();
        final Map<String, Object> tags = span.tags();

        assertThat(tags, hasEntry("error", true));

        final List<MockSpan.LogEntry> logs = span.logEntries();
        assertThat(logs, hasSize(3));

        assertThat(logs.get(0).fields(), hasEntry(equalTo("message"),
                hasFeature(Objects::toString, containsString("Error"))));
        assertThat(logs.get(1).fields(),
                hasEntry("error.kind", "NestedServletException"));
        assertThat((Throwable) logs.get(1).fields().get("error.object"),
                hasFeature(Throwables::getRootCause,
                        instanceOf(UnsupportedOperationException.class)));
        assertThat(logs.get(2).fields(),
                hasEntry(equalTo("stack"), instanceOf(String.class)));
    }

    @SneakyThrows
    private void waitFor(final Duration duration) {
        // not exactly sure why this is needed, but it is for some reason
        Thread.sleep(duration.toMillis());
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
