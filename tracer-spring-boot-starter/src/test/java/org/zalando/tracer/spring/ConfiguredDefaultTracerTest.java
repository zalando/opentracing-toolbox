package org.zalando.tracer.spring;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.zalando.tracer.Tracer;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.springframework.test.annotation.DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD;

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = Application.class)
@ImportAutoConfiguration(TracerAutoConfiguration.class)
@ActiveProfiles("uuid")
@DirtiesContext(classMode = AFTER_EACH_TEST_METHOD)
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
