package org.zalando.opentracing.kotlin

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.collections.atLeastSize
import io.kotest.matchers.maps.shouldContainAll
import io.kotest.matchers.maps.shouldContainExactly
import io.kotest.matchers.maps.shouldNotContainKey
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldHave
import io.opentracing.Span
import io.opentracing.mock.MockSpan
import io.opentracing.mock.MockTracer
import io.opentracing.tag.Tags
import org.junit.jupiter.api.assertThrows

class TracerExtensionsTest : FunSpec({

    test("trace successful") {
        val tracer = MockTracer()
        var span: Span? = null
        tracer.trace(
            operation = "mock",
            component = "test"
        ) {
            it.setTag("works", true)
            span = it
        }

        (span as MockSpan)
            .tags() shouldContainAll mapOf<String, Any>(
            "works" to true,
            Tags.COMPONENT.key to "test"
        )

        tracer.injectToMap(span)?.shouldContainExactly(
            mapOf(
                "spanid" to "2",
                "traceid" to "1"
            )
        )
    }

    test("get context from map") {
        val tracer = MockTracer()

        tracer.extract(
            mapOf(
                "spanid" to "2",
                "traceid" to "1"
            )
        ).let {
            it?.toSpanId() shouldBe "2"
            it?.toTraceId() shouldBe "1"
        }
    }

    test("trace failure") {
        val tracer = MockTracer()
        var span: Span? = null
        assertThrows<Exception> {
            tracer.trace(
                operation = "mock",
                component = "test"
            ) {
                it.setTag("works", true)
                span = it
                throw Exception()
                it.setTag("not_work", true)
            }
        }
        (span as MockSpan).let {
            it.tags() shouldContainAll mapOf<String, Any>(
                "works" to true,
                Tags.COMPONENT.key to "test",
                Tags.ERROR.key to true
            )

            it.tags() shouldNotContainKey "not_work"
            it.logEntries() shouldHave atLeastSize(1)
        }
    }
})
