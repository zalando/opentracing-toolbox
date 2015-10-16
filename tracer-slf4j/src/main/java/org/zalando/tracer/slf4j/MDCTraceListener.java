package org.zalando.tracer.slf4j;

/*
 * ⁣​
 * Tracer: SLF4J
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

import org.slf4j.MDC;
import org.zalando.tracer.TraceListener;

public final class MDCTraceListener implements TraceListener {

    @Override
    public void onStart(final String name, final String value) {
        MDC.put(name, value);
    }

    @Override
    public void onStop(final String name, final String value) {
        MDC.remove(name);
    }

}
