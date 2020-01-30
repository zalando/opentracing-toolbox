package org.zalando.opentracing.proxy.core;

import com.google.common.collect.ClassToInstanceMap;
import com.google.common.collect.ImmutableClassToInstanceMap;
import com.google.common.collect.ImmutableMap;
import lombok.AllArgsConstructor;
import org.zalando.opentracing.proxy.intercept.baggage.BaggageInterceptor;
import org.zalando.opentracing.proxy.intercept.injection.Injection;
import org.zalando.opentracing.proxy.intercept.log.LogInterceptor;
import org.zalando.opentracing.proxy.intercept.name.Naming;
import org.zalando.opentracing.proxy.intercept.span.SpanBuilderInterceptor;
import org.zalando.opentracing.proxy.intercept.tag.TagInterceptor;
import org.zalando.opentracing.proxy.listen.baggage.BaggageListener;
import org.zalando.opentracing.proxy.listen.log.LogListener;
import org.zalando.opentracing.proxy.listen.scope.ScopeListener;
import org.zalando.opentracing.proxy.listen.span.SpanListener;
import org.zalando.opentracing.proxy.listen.tag.TagListener;
import org.zalando.opentracing.proxy.spi.Plugin;
import org.zalando.opentracing.proxy.spi.Registry;

import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.function.BinaryOperator;

import static com.google.common.collect.ImmutableClassToInstanceMap.copyOf;

@AllArgsConstructor
final class PluginRegistry implements Registry, Plugins {

    private static final Map<Class<? extends Plugin>, Reducer<? extends Plugin>> reducers =
            ImmutableMap.<Class<? extends Plugin>, Reducer<? extends Plugin>>builder()
                    .put(Naming.class, reduce(Naming.DEFAULT, (left, right) -> right))
                    .put(SpanBuilderInterceptor.class, reduce(SpanBuilderInterceptor.DEFAULT, SpanBuilderInterceptor::composite))
                    .put(TagInterceptor.class, reduce(TagInterceptor.DEFAULT, TagInterceptor::composite))
                    .put(LogInterceptor.class, reduce(LogInterceptor.DEFAULT, LogInterceptor::composite))
                    .put(BaggageInterceptor.class, reduce(BaggageInterceptor.DEFAULT, BaggageInterceptor::composite))
                    .put(Injection.class, reduce(Injection.DEFAULT, Injection::composite))
                    .put(SpanListener.class, reduce(SpanListener.DEFAULT, SpanListener::composite))
                    .put(TagListener.class, reduce(TagListener.DEFAULT, TagListener::composite))
                    .put(LogListener.class, reduce(LogListener.DEFAULT, LogListener::composite))
                    .put(BaggageListener.class, reduce(BaggageListener.DEFAULT, BaggageListener::composite))
                    .put(ScopeListener.class, reduce(ScopeListener.DEFAULT, ScopeListener::composite))
            .build();

    private final ClassToInstanceMap<Plugin> plugins;
    private final Interceptors interceptors = new MyInterceptors();
    private final Listeners listeners = new MyListeners();

    PluginRegistry() {
        this(ImmutableClassToInstanceMap.of());
    }

    @FunctionalInterface
    private interface Reducer<P extends Plugin> {

        default P reduce(final P left, final P right) {
            return reduce(Arrays.asList(left, right));
        }

        P reduce(Collection<P> plugins);

    }

    private static <P extends Plugin> Reducer<P> reduce(
            final P seed, final BinaryOperator<P> merge) {
        return plugins -> plugins.stream().reduce(merge).orElse(seed);
    }

    @Override
    public PluginRegistry register(final Plugin plugin) {
        final Map<Class<? extends Plugin>, Plugin> result =
                new HashMap<>(plugins);

        reducers.forEach((type, raw) -> {
            if (type.isInstance(plugin)) {
                @SuppressWarnings("unchecked")
                final Reducer<Plugin> reducer = (Reducer<Plugin>) raw;

                result.merge(type, plugin, reducer::reduce);
            }
        });

        return new PluginRegistry(copyOf(result));
    }
    
    @Override
    public Interceptors interceptors() {
        return interceptors;
    }

    @Override
    public Listeners listeners() {
        return listeners;
    }
    
    private <P extends Plugin> P plugin(final Class<P> type, final P defaultValue) {
        @Nullable final P instance = plugins.getInstance(type);
        return instance == null ? defaultValue : instance;
    }

    private class MyInterceptors implements Interceptors {

        @Override
        public Naming names() {
            return plugin(Naming.class, Naming.DEFAULT);
        }

        @Override
        public SpanBuilderInterceptor spans() {
            return plugin(SpanBuilderInterceptor.class, SpanBuilderInterceptor.DEFAULT);
        }

        @Override
        public TagInterceptor tags() {
            return plugin(TagInterceptor.class, TagInterceptor.DEFAULT);
        }

        @Override
        public LogInterceptor logs() {
            return plugin(LogInterceptor.class, LogInterceptor.DEFAULT);
        }

        @Override
        public BaggageInterceptor baggage() {
            return plugin(BaggageInterceptor.class, BaggageInterceptor.DEFAULT);
        }

        @Override
        public Injection injections() {
            return plugin(Injection.class, Injection.DEFAULT);
        }

    }

    private final class MyListeners implements Listeners {

        @Override
        public SpanListener spans() {
            return plugin(SpanListener.class, SpanListener.DEFAULT);
        }

        @Override
        public TagListener tags() {
            return plugin(TagListener.class, TagListener.DEFAULT);
        }

        @Override
        public LogListener logs() {
            return plugin(LogListener.class, LogListener.DEFAULT);
        }

        @Override
        public BaggageListener baggage() {
            return plugin(BaggageListener.class, BaggageListener.DEFAULT);
        }

        @Override
        public ScopeListener scopes() {
            return plugin(ScopeListener.class, ScopeListener.DEFAULT);
        }

    }

}
