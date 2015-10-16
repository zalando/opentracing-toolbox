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

import org.junit.rules.TestWatcher;
import org.junit.runner.Description;
import org.slf4j.MDC;

public final class MDCRule extends TestWatcher {

    @Override
    protected void starting(final Description description) {
        MDC.clear();
    }

    @Override
    protected void finished(final Description description) {
        MDC.clear();
    }

}
