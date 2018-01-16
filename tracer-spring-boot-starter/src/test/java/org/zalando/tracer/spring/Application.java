package org.zalando.tracer.spring;

import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@ImportAutoConfiguration(TracerAutoConfiguration.class)
public class Application {
}
