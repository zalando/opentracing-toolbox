package org.zalando.opentracing.jdbc;

import io.opentracing.Span;
import lombok.AllArgsConstructor;
import lombok.experimental.Delegate;

// TODO this is not a proper implementation - needs to return this everywhere!
@AllArgsConstructor
abstract class ForwardingSpan implements Span {

    @Delegate
    protected abstract Span delegate();

}
