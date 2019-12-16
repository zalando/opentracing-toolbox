package org.zalando.opentracing.servlet.extension;

import io.opentracing.contrib.web.servlet.filter.ServletFilterSpanDecorator;
import lombok.AllArgsConstructor;
import lombok.experimental.Delegate;

@AllArgsConstructor
abstract class ForwardingSpanDecorator implements ServletFilterSpanDecorator {

    @Delegate
    private final ServletFilterSpanDecorator decorator;

}
