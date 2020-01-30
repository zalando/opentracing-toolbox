package org.zalando.opentracing.proxy.intercept.log;

import io.opentracing.Span;
import lombok.AllArgsConstructor;
import org.zalando.opentracing.proxy.listen.log.LogListener;
import org.zalando.opentracing.proxy.intercept.log.LogInterceptor.Log;

import java.util.function.BiConsumer;
import java.util.function.Consumer;

@AllArgsConstructor
final class DefaultLog implements Log {

    private final Consumer<Span> log;
    private final BiConsumer<LogListener, Span> notify;

    @Override
    public void log(final Span span) {
        log.accept(span);
    }

    @Override
    public void notify(final LogListener listener, final Span span) {
        notify.accept(listener, span);
    }

}
