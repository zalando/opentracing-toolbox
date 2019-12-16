package org.zalando.opentracing.spring.web.extension;

import io.opentracing.Span;
import io.opentracing.tag.StringTag;
import io.opentracing.tag.Tag;
import org.apiguardian.api.API;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.method.HandlerMethod;

import javax.servlet.http.HttpServletRequest;
import java.util.Optional;

import static org.apiguardian.api.API.Status.EXPERIMENTAL;

@API(status = EXPERIMENTAL)
public final class HttpPathSpanDecorator
        implements SpanDecorator {

    private static final Tag<String> HTTP_PATH = new StringTag("http.path");

    @Override
    public void onPreHandle(
            final HttpServletRequest request,
            final Object handler,
            final Span span) {

        Optional.of(handler)
                .filter(HandlerMethod.class::isInstance)
                .map(HandlerMethod.class::cast)
                .map(method -> method.getMethodAnnotation(RequestMapping.class))
                .map(RequestMapping::path)
                .map(paths -> paths.length == 0 ? null : paths[0])
                .ifPresent(path -> span.setTag(HTTP_PATH, path));
    }

}
