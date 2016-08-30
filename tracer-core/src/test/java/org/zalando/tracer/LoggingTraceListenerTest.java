package org.zalando.tracer;

import org.junit.Test;
import org.slf4j.Logger;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.sameInstance;
import static org.hobsoft.hamcrest.compose.ComposeMatchers.hasFeature;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.hamcrest.MockitoHamcrest.argThat;

public final class LoggingTraceListenerTest {

    private final Logger logger = mock(Logger.class);
    private final LoggingTraceListener listener = new LoggingTraceListener(logger);
    private final Tracer tracer = Tracer.builder()
            .trace("X-Trace-ID", () -> "19c532c8-751f-11e5-a11d-10ddb1ee7671")
            .listener(listener)
            .build();

    @Test
    public void shouldLogOnStart() throws Exception {
        tracer.start();

        verify(logger).trace(argThat(containsString("Started")),
                anyString(),
                argThat(containsString("19c532c8-751f-11e5-a11d-10ddb1ee7671")));
    }

    @Test
    public void shouldLogOnStop() throws Exception {
        tracer.start();
        tracer.stop();


        verify(logger).trace(argThat(containsString("Stopped")),
                anyString(),
                argThat(containsString("19c532c8-751f-11e5-a11d-10ddb1ee7671")));
    }

    @Test
    public void shouldProvideAccessToLoggerForTestingPurposes() {
        assertThat(listener, hasFeature("logger", LoggingTraceListener::getLogger, is(sameInstance(logger))));
    }

}