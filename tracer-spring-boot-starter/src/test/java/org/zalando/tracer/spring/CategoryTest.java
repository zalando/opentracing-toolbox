package org.zalando.tracer.spring;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.zalando.tracer.LoggingTraceListener;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hobsoft.hamcrest.compose.ComposeMatchers.hasFeature;

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = Application.class, properties = {
        "tracer.logging.enabled = true",
        "tracer.logging.category = http.trace-log"
})
@ImportAutoConfiguration(TracerAutoConfiguration.class)
final class CategoryTest {

    @Autowired
    private LoggingTraceListener loggingTraceListener;

    @Test
    void shouldUseConfiguredCategory() {
        final Logger logger = loggingTraceListener.getLogger();
        assertThat(logger, hasFeature("name", Logger::getName, is("http.trace-log")));
    }

}
