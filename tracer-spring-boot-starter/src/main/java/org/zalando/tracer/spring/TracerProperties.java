package org.zalando.tracer.spring;

import org.apiguardian.api.API;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.LinkedHashMap;
import java.util.Map;

import static org.apiguardian.api.API.Status.INTERNAL;

@API(status = INTERNAL)
@ConfigurationProperties(prefix = "tracer")
public final class TracerProperties {

    private boolean stacked;
    private final Logging logging = new Logging();
    private final Map<String, String> traces = new LinkedHashMap<>();

    public boolean isStacked() {
        return stacked;
    }

    public void setStacked(final boolean stacked) {
        this.stacked = stacked;
    }

    public Logging getLogging() {
        return logging;
    }

    public Map<String, String> getTraces() {
        return traces;
    }

    public static class Logging {

        private String category;

        public String getCategory() {
            return category;
        }

        public void setCategory(final String category) {
            this.category = category;
        }
    }

}
