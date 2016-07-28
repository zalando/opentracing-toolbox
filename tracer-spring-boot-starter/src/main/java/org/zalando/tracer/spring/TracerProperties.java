package org.zalando.tracer.spring;

/*
 * ⁣​
 * Tracer: Servlet
 * ⁣⁣
 * Copyright (C) 2015 Zalando SE
 * ⁣⁣
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * ​⁣
 */

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.LinkedHashMap;
import java.util.Map;

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
