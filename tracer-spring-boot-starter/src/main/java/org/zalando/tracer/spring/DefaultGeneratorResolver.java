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

import com.google.common.collect.ImmutableMap;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.stereotype.Component;
import org.zalando.tracer.FlowIDGenerator;
import org.zalando.tracer.Generator;
import org.zalando.tracer.UUIDGenerator;

import java.util.Locale;
import java.util.Optional;

@Component
@ConditionalOnMissingBean(GeneratorResolver.class)
class DefaultGeneratorResolver implements GeneratorResolver {

    private final ImmutableMap<String, Generator> generators = ImmutableMap.of(
            "flow-id", new FlowIDGenerator(),
            "uuid", new UUIDGenerator());

    @Override
    public Generator resolve(String name) {
        return Optional.ofNullable(generators.get(name.toLowerCase(Locale.ROOT)))
                .orElseThrow(UnsupportedOperationException::new);
    }

}
