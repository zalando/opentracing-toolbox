package org.zalando.tracer.spring;

import org.junit.Test;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.TestPropertySource;
import org.zalando.tracer.LoggingTraceListener;

import java.io.IOException;

import static org.hamcrest.Matchers.is;
import static org.hobsoft.hamcrest.compose.ComposeMatchers.hasFeature;
import static org.junit.Assert.assertThat;

@TestPropertySource(properties = {
        "tracer.logging.enabled = true",
        "tracer.logging.category = http.trace-log"
})
public final class CategoryTest extends AbstractTest {

    @Autowired
    private LoggingTraceListener loggingTraceListener;

    @Test
    public void shouldUseConfiguredCategory() throws IOException {
        final Logger logger = loggingTraceListener.getLogger();
        assertThat(logger, hasFeature("name", Logger::getName, is("http.trace-log")));
    }

}
