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

import org.junit.Test;

import java.util.concurrent.Callable;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.sameInstance;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public final class ClosureTest {

    @Test
    public void shouldReturnResultFromCallable() throws Exception {
        final Callable<?> callable = mock(Callable.class);
        final Closure<?, Exception> unit = Closure.valueOf(callable);

        when(callable.call()).thenReturn(Boolean.TRUE);
        final Object result = unit.run();

        assertThat(result, is(sameInstance(Boolean.TRUE)));
    }

    @Test
    public void shouldInvokeCallableExactlyOnce() throws Exception {
        final Callable<?> callable = mock(Callable.class);
        final Closure<?, Exception> unit = Closure.valueOf(callable);

        unit.run();

        verify(callable, times(1)).call();
    }

    @Test
    public void shouldInvokeRunnableExactlyOnce() {
        final Runnable runnable = mock(Runnable.class);
        final Closure<Void, RuntimeException> unit = Closure.valueOf(runnable);

        unit.run();

        verify(runnable, times(1)).run();
    }

}