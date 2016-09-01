package org.zalando.tracer.spring;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.TestPropertySource;
import org.zalando.tracer.LoggingTraceListener;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.assertThat;

@TestPropertySource(properties = "tracer.logging.enabled = true")
public final class EnabledLoggingTest extends AbstractTest {

    @Autowired
    private LoggingTraceListener loggingTraceListener;

    @Test
    public void shouldDisableLoggingTraceListenerByDefault() {
        assertThat(loggingTraceListener, is(notNullValue()));
    }

}
