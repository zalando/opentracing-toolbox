package org.zalando.opentracing.jdbc.autoconfigure;

import lombok.AllArgsConstructor;
import org.springframework.aop.framework.ProxyFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;

import java.util.function.UnaryOperator;

@AllArgsConstructor
final class ConditionalProxyProcessor<T> implements BeanPostProcessor {

    private final Class<T> type;
    private final UnaryOperator<T> operator;

    @Override
    public Object postProcessBeforeInitialization(final Object bean, final String name) throws BeansException {
        return bean;
    }

    @Override
    public Object postProcessAfterInitialization(final Object bean, final String name) throws BeansException {
        if (type.isInstance(bean)) {
            final T original = type.cast(bean);
            final T applied = operator.apply(original);
            return proxy(original, applied);
        }

        return bean;
    }

    private Object proxy(final T original, final T applied) {
        final ProxyFactory factory = new ProxyFactory(original);
        factory.setProxyTargetClass(true);
        factory.addAdvice(new ConditionalMethodInterceptor(applied));
        return factory.getProxy();
    }

}
