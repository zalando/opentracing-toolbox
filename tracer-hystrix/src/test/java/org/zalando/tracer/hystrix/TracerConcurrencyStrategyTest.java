package org.zalando.tracer.hystrix;

/*
 * ⁣​
 * Tracer: Hystrix
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

import com.hystrix.junit.HystrixRequestContextRule;
import com.netflix.hystrix.HystrixCommand;
import com.netflix.hystrix.HystrixCommandGroupKey;
import com.netflix.hystrix.strategy.HystrixPlugins;
import com.netflix.hystrix.strategy.concurrency.HystrixConcurrencyStrategy;
import com.netflix.hystrix.strategy.concurrency.HystrixConcurrencyStrategyDefault;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.zalando.tracer.Trace;
import org.zalando.tracer.Tracer;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

public final class TracerConcurrencyStrategyTest {

    @Rule
    public HystrixRequestContextRule hystrix = new HystrixRequestContextRule();

    private final Tracer tracer = Tracer.builder()
            .trace("X-Trace", () -> "76f6046c-1b56-11e6-8c85-8fc9ee29f631")
            .build();
    private final Trace trace = tracer.get("X-Trace");

    @Before
    public void setUp() {
        final HystrixPlugins plugins = HystrixPlugins.getInstance();
        final HystrixConcurrencyStrategy delegate = HystrixConcurrencyStrategyDefault.getInstance();
        plugins.registerConcurrencyStrategy(new TracerConcurrencyStrategy(tracer, delegate));
    }

    @Test
    public void shouldGetTrace() {
        tracer.start();

        try {
            final String traceId = new GetTrace().execute();
            assertThat(traceId, is("76f6046c-1b56-11e6-8c85-8fc9ee29f631"));
        } finally {
            tracer.stop();
        }
    }

    private final class GetTrace extends HystrixCommand<String> {

        public GetTrace() {
            super(HystrixCommandGroupKey.Factory.asKey("ExampleGroup"));
        }

        @Override
        protected String run() {
            return trace.getValue();
        }

    }

}