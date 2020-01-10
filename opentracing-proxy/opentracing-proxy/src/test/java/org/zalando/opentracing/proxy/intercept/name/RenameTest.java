package org.zalando.opentracing.proxy.intercept.name;

import com.google.common.base.CaseFormat;
import io.opentracing.Tracer;
import io.opentracing.mock.MockSpan;
import io.opentracing.mock.MockTracer;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.zalando.opentracing.proxy.core.ProxyTracer;

import java.util.Arrays;

import static com.google.common.base.CaseFormat.LOWER_CAMEL;
import static com.google.common.base.CaseFormat.LOWER_HYPHEN;
import static com.google.common.base.CaseFormat.LOWER_UNDERSCORE;
import static com.google.common.base.CaseFormat.UPPER_CAMEL;
import static com.google.common.base.CaseFormat.UPPER_UNDERSCORE;
import static com.google.common.collect.Iterables.getOnlyElement;
import static org.junit.jupiter.api.Assertions.assertEquals;

class RenameTest {

    static Iterable<Arguments> data() {
        return Arrays.asList(
                Arguments.of("detectFormat", LOWER_UNDERSCORE, "detect_format"),
                Arguments.of("detectFormat", UPPER_UNDERSCORE, "DETECT_FORMAT"),
                Arguments.of("detectFormat", LOWER_HYPHEN, "detect-format"),
                Arguments.of("detectFormat", LOWER_CAMEL, "detectFormat"),
                Arguments.of("detectFormat", UPPER_CAMEL, "DetectFormat")
        );
    }

    @ParameterizedTest
    @MethodSource("data")
    void renamesOperationName(
            final String source,
            final CaseFormat targetFormat,
            final String target) {

        final MockTracer tracer = new MockTracer();
        final Tracer unit = new ProxyTracer(tracer)
                .with(new Rename(targetFormat));

        unit.buildSpan(source)
                .start().finish();

        final MockSpan span = getOnlyElement(tracer.finishedSpans());
        assertEquals(target, span.operationName());
    }

    @ParameterizedTest
    @MethodSource("data")
    void renamesOperationOnSet(
            final String source,
            final CaseFormat targetFormat,
            final String target) {

        final MockTracer tracer = new MockTracer();
        final Tracer unit = new ProxyTracer(tracer)
                .with(new Rename(targetFormat));

        unit.buildSpan("test")
                .start()
                .setOperationName(source)
                .finish();

        final MockSpan span = getOnlyElement(tracer.finishedSpans());
        assertEquals(target, span.operationName());
    }

    @ParameterizedTest
    @MethodSource("data")
    void overridesPreviousNaming(
            final String source,
            final CaseFormat targetFormat,
            final String target) {

        final MockTracer tracer = new MockTracer();
        final Tracer unit = new ProxyTracer(tracer)
                .with(Naming.DEFAULT)
                .with(new Rename(targetFormat));

        unit.buildSpan(source)
                .start().finish();

        final MockSpan span = getOnlyElement(tracer.finishedSpans());
        assertEquals(target, span.operationName());
    }

}
