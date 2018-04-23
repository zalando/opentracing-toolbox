package org.zalando.tracer.benchmark;

import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.infra.Blackhole;
import org.zalando.tracer.Generator;
import org.zalando.tracer.Random128Generator;

@State(Scope.Benchmark)
public class Random128GeneratorBenchmark {

    private final Generator generator = new Random128Generator();

    @Benchmark
    public void benchmark(final Blackhole blackhole) {
        blackhole.consume(generator.generate());
    }

}
