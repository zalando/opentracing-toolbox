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
import java.util.Arrays;
import java.util.Iterator;
import org.junit.Test;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.matchesPattern;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.containsString;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

public class GeneratorTest {

    @Test
    public void testUuid() {
        final String value = new UUIDGenerator().generate();

        assertThat(value.length(), is(36));
    }

    @Test
    public void testFlowId() throws Exception {
        final String value = new FlowIDGenerator().generate();

        assertThat(value.length(), is(22));
    }

    @Test
    public void testPhrase() throws Exception {
        final String value = new PhraseGenerator().generate();

        assertThat(value, matchesPattern("[a-z]+_[a-z]+_[a-z]+_[a-z]+_[a-z]+"));

        assertTrue(PhraseGenerator.isJacocoHappy());
    }

    @Test
    public void testPhraseWozniakIsNotBoring() throws Exception {
        final Iterator<Integer> indexes = Arrays.asList(9, 143, 0, 0, 0, 0, 0, 0, 0, 0).iterator();

        final String value = PhraseGenerator.generate(i -> indexes.next());

        assertThat(value, is(not(containsString("boring_wozniak"))));
    }

    @Test
    public void testPhraseOver_1_000_000_000() throws Exception {
        assertTrue(PhraseGenerator.maxCombinations() > 1_000_000_000);
    }

    @Test
    public void testPhraseMinLength() throws Exception {
        assertThat(PhraseGenerator.minLength(), is(22));
    }

    @Test
    public void testPhraseMaxLength() throws Exception {
        assertThat(PhraseGenerator.maxLength(), is(61));
    }
}
