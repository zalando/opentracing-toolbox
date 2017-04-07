package org.zalando.tracer.spring;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.LinkedHashMap;
import java.util.Map;

@ConfigurationProperties(prefix = "tracer")
public final class TracerProperties {

    private boolean append;
    private boolean stacked;
    private final Logging logging = new Logging();
    private final Map<String, String> traces = new LinkedHashMap<>();

    public boolean isAppend() {
        return append;
    }

    public void setAppend(final boolean append) {
        this.append = append;
    }

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
