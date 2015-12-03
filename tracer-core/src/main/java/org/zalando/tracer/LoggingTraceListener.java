package org.zalando.tracer;

/*
 * ⁣​
 * Tracer
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

import com.google.common.annotations.VisibleForTesting;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class LoggingTraceListener implements TraceListener {

    private final Logger logger;

    public LoggingTraceListener() {
        this(LoggerFactory.getLogger(Tracer.class));
    }

    public LoggingTraceListener(final Logger logger) {
        this.logger = logger;
    }

    @VisibleForTesting
    public Logger getLogger() {
        return logger;
    }

    @Override
    public void onStart(final String name, final String value) {
        logger.trace("Started [{}] trace: [{}]", name, value);
    }

    @Override
    public void onStop(final String name, final String value) {
        logger.trace("Stopped [{}] trace: [{}]", name, value);
    }

}
