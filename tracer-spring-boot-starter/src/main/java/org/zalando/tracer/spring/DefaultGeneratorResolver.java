package org.zalando.tracer.spring;

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.stereotype.Component;
import org.zalando.tracer.FlowIDGenerator;
import org.zalando.tracer.Generator;
import org.zalando.tracer.PhraseGenerator;
import org.zalando.tracer.Random128Generator;
import org.zalando.tracer.Random64Generator;
import org.zalando.tracer.UUIDGenerator;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;

@Component
@ConditionalOnMissingBean(GeneratorResolver.class)
class DefaultGeneratorResolver implements GeneratorResolver {

    private final Map<String, Generator> generators;

    DefaultGeneratorResolver() {
        final Map<String, Generator> map = new LinkedHashMap<>();
        map.put("flow-id", new FlowIDGenerator());
        map.put("uuid", new UUIDGenerator());
        map.put("phrase", new PhraseGenerator());
        map.put("random64", new Random64Generator());
        map.put("random128", new Random128Generator());
        this.generators = Collections.unmodifiableMap(map);
    }

    @Override
    public Generator resolve(final String name) {
        return Optional.ofNullable(generators.get(name.toLowerCase(Locale.ROOT)))
                .orElseThrow(UnsupportedOperationException::new);
    }

}
