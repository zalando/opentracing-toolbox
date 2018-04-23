package org.zalando.tracer.benchmark;

import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.infra.Blackhole;
import org.zalando.tracer.FlowIDGenerator;
import org.zalando.tracer.Generator;

@State(Scope.Benchmark)
public class FlowIDGeneratorBenchmark {

    private final Generator generator = new FlowIDGenerator();

    @Benchmark
    public void benchmark(final Blackhole blackhole) {
        blackhole.consume(generator.generate());
    }

}
