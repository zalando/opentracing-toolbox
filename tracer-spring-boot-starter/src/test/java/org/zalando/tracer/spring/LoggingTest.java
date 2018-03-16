package org.zalando.tracer.spring;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.zalando.tracer.LoggingTraceListener;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = Application.class)
@ImportAutoConfiguration(TracerAutoConfiguration.class)
final class LoggingTest {

    @Autowired(required = false)
    private LoggingTraceListener loggingTraceListener;

    @Test
    void shouldDisableLoggingTraceListenerByDefault() {
        assertThat(loggingTraceListener, is(nullValue()));
    }

}
