package org.zalando.tracer.spring;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.zalando.tracer.Tracer;

import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = Application.class)
@ImportAutoConfiguration(TracerAutoConfiguration.class)
@ActiveProfiles("uuid")
final class ConfiguredDefaultTracerTest {

    @Autowired
    private Tracer tracer;

    @Test
    void shouldBeDefault() {
        tracer.start();
        tracer.get("X-Trace-ID");

        assertThrows(IllegalStateException.class, tracer::start);
    }

}
