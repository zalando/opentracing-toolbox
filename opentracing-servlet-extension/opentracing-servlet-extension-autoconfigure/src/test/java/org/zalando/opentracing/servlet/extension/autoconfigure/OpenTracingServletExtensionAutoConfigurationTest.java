package org.zalando.opentracing.servlet.extension.autoconfigure;

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
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.web.client.DefaultResponseErrorHandler;
import org.springframework.web.client.RestTemplate;

import java.time.Duration;
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
import static org.springframework.http.HttpStatus.OK;

@ExtendWith(SpringExtension.class) // only needed for Spring 4 support
@SpringBootTest(
        webEnvironment = DEFINED_PORT,
        properties = {
                "spring.main.web-application-type: servlet",
                "opentracing.spring.web.client.enabled: false"
        })
@EnableAutoConfiguration(excludeName = {
        "org.springframework.boot.autoconfigure.web.ErrorMvcAutoConfiguration",
        "org.springframework.boot.autoconfigure.web.servlet.error.ErrorMvcAutoConfiguration"
})
class OpenTracingServletExtensionAutoConfigurationTest {

    @Configuration
    @Import(ExampleController.class)
    static class TestConfiguration {

        @Bean
        public MockTracer tracer() {
            return new MockTracer();
        }

        // Ideally we would depend on ServerTracingAutoConfiguration, but we can't
        // as long as we want to support Spring 4, hence we fake it
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

    }

    private final RestTemplate client = new RestTemplate();

    @Autowired
    private MockTracer tracer;

    public OpenTracingServletExtensionAutoConfigurationTest() {
        this.client.setErrorHandler(new DefaultResponseErrorHandler() {
            @Override
            public void handleError(final ClientHttpResponse response) {
                // nothing to do
            }
        });
    }

    @BeforeEach
    @AfterEach
    void setUp() {
        tracer.reset();
    }

    @Test
    void shouldTag() {
        final ResponseEntity<String> response = client.getForEntity(
                "http://localhost:8080/greet?name=Alice", String.class);

        assertThat(response.getStatusCode(), is(OK));
        assertThat(response.getBody(), is("Hello, Alice!"));

        waitFor(Duration.ofSeconds(1));

        final Map<String, Object> tags = tags();

        assertThat(tags, hasEntry("component", "Servlet"));
        assertThat(tags, hasEntry("http.method", "GET"));
        assertThat(tags, hasEntry("peer.ipv4", "127.0.0.1"));

        assertThat(tags, not(hasKey("http.url")));
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
