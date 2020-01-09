package org.zalando.opentracing.jdbc.autoconfigure;

import lombok.AllArgsConstructor;
import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;

import javax.annotation.Nullable;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Collection;

import static lombok.AccessLevel.PRIVATE;
import static org.springframework.aop.support.AopUtils.invokeJoinpointUsingReflection;

/**
 * This interceptor is conditionally forwarding methods to a given delegate in
 * case that particular method is available. The purpose is to preserve
 * reflection capabilities on original instance.
 *
 * @see <a href="https://stackoverflow.com/questions/44237787/how-to-use-user-defined-database-proxy-in-datajpatest">How to use user defined database proxy in @DataJpaTest</a>
 * @see <a href="https://gitter.im/spring-projects/spring-boot?at=5983602d2723db8d5e70a904">Spring Boot Gitter</a>
 */
@AllArgsConstructor(access = PRIVATE)
final class ConditionalMethodInterceptor implements MethodInterceptor {

    private final Object delegate;
    private final Collection<Method> methods;

    public ConditionalMethodInterceptor(final Object delegate) {
        this(delegate, Arrays.asList(delegate.getClass().getMethods()));
    }

    @Override
    public Object invoke(final MethodInvocation invocation) throws Throwable {
        final Method method = find(invocation.getMethod());

        if (method == null) {
            return invocation.proceed();
        }

        final Object[] arguments = invocation.getArguments();
        return invokeJoinpointUsingReflection(delegate, method, arguments);
    }

    @Nullable
    private Method find(final Method method) {
        for (final Method candidate : methods) {
            if (sameName(method, candidate) &&
                    sameParameters(method, candidate)) {
                return candidate;
            }
        }

        return null;
    }

    private boolean sameName(final Method left, final Method right) {
        return left.getName().equals(right.getName());
    }

    private boolean sameParameters(final Method left, final Method right) {
        return Arrays.equals(
                left.getParameterTypes(),
                right.getParameterTypes());
    }

}
