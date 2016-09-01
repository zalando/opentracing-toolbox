package org.zalando.tracer.servlet;

import org.junit.Rule;
import org.junit.Test;
import org.zalando.tracer.Trace;
import org.zalando.tracer.Tracer;

import static com.jayway.restassured.RestAssured.given;
import static java.lang.String.format;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

public final class TracerFilterLifecycleTest {

    private final Tracer tracer = mock(Tracer.class);

    @Rule
    public final JettyRule jetty = new JettyRule(new TracerFilter(tracer), mock(Trace.class));

    private String url(final String path) {
        return format("http://localhost:%d%s", jetty.getPort(), path);
    }

    @Test
    public void shouldStartAndStopTracer() throws Exception {
        given().
                when()
                .get(url("/traced"));

        verify(tracer, times(1)).start(any());
        verify(tracer, times(1)).stop();
    }

    @Test
    public void shouldStartAndStopTracerOnFailure() throws Exception {
        given().
                when()
                .get(url("/failure"));

        verify(tracer, times(1)).start(any());
        verify(tracer, times(1)).stop();
    }

    @Test
    public void shouldIgnoreTracerWhenTraceIsNotActive() throws Exception {
        given().
                when()
                .get(url("/untraced"));

        verifyNoMoreInteractions(tracer);
    }

}