package org.zalando.tracer.spring;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.zalando.tracer.LoggingTraceListener;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.assertThat;

public final class LoggingTest extends AbstractTest {

    @Autowired(required = false)
    private LoggingTraceListener loggingTraceListener;

    @Test
    public void shouldDisableLoggingTraceListenerByDefault() {
        assertThat(loggingTraceListener, is(nullValue()));
    }

}
