package org.zalando.tracer.benchmark;

import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.infra.Blackhole;
import org.zalando.tracer.Generator;
import org.zalando.tracer.UUIDGenerator;

@State(Scope.Benchmark)
public class UUIDGeneratorBenchmark {

    private final Generator generator = new UUIDGenerator();

    @Benchmark
    public void benchmark(final Blackhole blackhole) {
        blackhole.consume(generator.generate());
    }

}
