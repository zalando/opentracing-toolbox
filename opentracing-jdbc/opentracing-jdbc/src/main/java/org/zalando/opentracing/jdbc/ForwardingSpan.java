package org.zalando.opentracing.jdbc;

import io.opentracing.Span;
import lombok.AllArgsConstructor;
import lombok.experimental.Delegate;

@AllArgsConstructor
abstract class ForwardingSpan implements Span {

    @Delegate
    protected abstract Span delegate();

}
