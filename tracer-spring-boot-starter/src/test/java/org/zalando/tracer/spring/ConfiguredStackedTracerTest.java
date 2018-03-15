package org.zalando.tracer.spring;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.zalando.tracer.Tracer;

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = Application.class, properties = "tracer.stacked = true")
@ImportAutoConfiguration(TracerAutoConfiguration.class)
@ActiveProfiles("uuid")
final class ConfiguredStackedTracerTest {

    @Autowired
    private Tracer tracer;

    @Test
    void shouldBeStacked() {
        tracer.start();
        tracer.get("X-Trace-ID");
        tracer.start();
    }

}
