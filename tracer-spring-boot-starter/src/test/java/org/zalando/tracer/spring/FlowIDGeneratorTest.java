package org.zalando.tracer.spring;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.zalando.tracer.Trace;
import org.zalando.tracer.Tracer;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hobsoft.hamcrest.compose.ComposeMatchers.hasFeature;

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = Application.class)
@ImportAutoConfiguration(TracerAutoConfiguration.class)
@ActiveProfiles("flow-id")
final class FlowIDGeneratorTest {

    @Autowired
    private Tracer tracer;

    @Test
    void shouldConfigureFlowIdTrace() {
        final Trace trace = tracer.get("X-Flow-ID");

        tracer.start();

        try {
            assertThat(trace, hasFeature("value", Trace::getValue,
                    hasFeature("length", String::length, is(22))));
        } finally {
            tracer.stop();
        }
    }

}
