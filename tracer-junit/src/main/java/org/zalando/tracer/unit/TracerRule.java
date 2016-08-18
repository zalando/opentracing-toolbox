package org.zalando.tracer.unit;

/*
 * ⁣​
 * Tracer: JUnit
 * ⁣⁣
 * Copyright (C) 2015 - 2016 Zalando SE
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
import org.zalando.tracer.Tracer;

import javax.inject.Inject;

public final class TracerRule extends TestWatcher {

    private final Tracer tracer;

    @Inject
    public TracerRule(final Tracer tracer) {
        this.tracer = tracer;
    }

    @Override
    protected void starting(final Description description) {
        tracer.start();
    }

    @Override
    protected void finished(final Description description) {
        tracer.stop();
    }

}
