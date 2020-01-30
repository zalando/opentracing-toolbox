package org.zalando.opentracing.proxy.core;

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

interface Plugins {

    Interceptors interceptors();
    Listeners listeners();

    interface Interceptors {
        Naming names();
        SpanBuilderInterceptor spans();
        TagInterceptor tags();
        LogInterceptor logs();
        BaggageInterceptor baggage();
        Injection injections();
    }

    interface Listeners {
        SpanListener spans();
        TagListener tags();
        LogListener logs();
        BaggageListener baggage();
        ScopeListener scopes();
    }

}
