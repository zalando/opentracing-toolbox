package org.zalando.opentracing.proxy.intercept.log;

import io.opentracing.Span;
import org.apiguardian.api.API;
import org.zalando.opentracing.proxy.listen.log.LogListener;
import org.zalando.opentracing.proxy.spi.Plugin;

import javax.annotation.CheckReturnValue;
import java.util.Arrays;
import java.util.Collection;
import java.util.Map;

import static java.util.Collections.singleton;
import static org.apiguardian.api.API.Status.EXPERIMENTAL;

@API(status = EXPERIMENTAL)
public interface LogInterceptor extends Plugin {

    LogInterceptor DEFAULT = new LogInterceptor() {
        // nothing to do
    };

    @CheckReturnValue
    default Collection<Log> intercept(
            final Span span,
            final long timestamp,
            final Map<String, ?> fields) {

        return singleton(Log.of(timestamp, fields));
    }

    @CheckReturnValue
    default Collection<Log> intercept(
            final Span span,
            final Map<String, ?> fields) {

        return singleton(Log.of(fields));
    }

    interface Log {
        void log(Span span);
        void notify(LogListener listener, Span span);

        static Log of(final long timestamp, final Map<String, ?> fields) {
            return new DefaultLog(
                    span -> span.log(timestamp, fields),
                    (listener, span) -> listener.onLog(span, timestamp, fields)
            );
        }

        static Log of(final Map<String, ?> fields) {
            return new DefaultLog(
                    span -> span.log(fields),
                    (listener, span) -> listener.onLog(span, fields)
            );
        }
    }

    static LogInterceptor composite(final LogInterceptor... interceptors) {
        return composite(Arrays.asList(interceptors));
    }

    static LogInterceptor composite(
            final Collection<LogInterceptor> interceptors) {

        return new CompositeLogInterceptor(interceptors);
    }

}
