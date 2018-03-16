package org.zalando.tracer.servlet;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.zalando.tracer.Trace;
import org.zalando.tracer.Tracer;

import static com.jayway.restassured.RestAssured.given;
import static java.lang.String.format;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

final class TracerFilterLifecycleTest {

    private final Tracer tracer = mock(Tracer.class);

    @RegisterExtension
    final JettyExtension jetty = new JettyExtension(new TracerFilter(tracer), mock(Trace.class));

    private String url(final String path) {
        return format("http://localhost:%d%s", jetty.getPort(), path);
    }

    @Test
    void shouldStartAndStopTracer() {
        given().
                when()
                .get(url("/traced"));

        verify(tracer, times(1)).start(any());
        verify(tracer, times(1)).stop();
    }

    @Test
    void shouldStartAndStopTracerOnFailure() {
        given().
                when()
                .get(url("/failure"));

        verify(tracer, times(1)).start(any());
        verify(tracer, times(1)).stop();
    }

    @Test
    void shouldIgnoreTracerWhenTraceIsNotActive() {
        given().
                when()
                .get(url("/untraced"));

        verifyNoMoreInteractions(tracer);
    }

}
