package org.zalando.opentracing.jdbc.autoconfigure;

import lombok.AllArgsConstructor;
import lombok.experimental.Delegate;
import org.springframework.beans.factory.config.BeanPostProcessor;

@AllArgsConstructor
abstract class ForwardingProcessor implements BeanPostProcessor {

    @Delegate
    private final BeanPostProcessor processor;

}
