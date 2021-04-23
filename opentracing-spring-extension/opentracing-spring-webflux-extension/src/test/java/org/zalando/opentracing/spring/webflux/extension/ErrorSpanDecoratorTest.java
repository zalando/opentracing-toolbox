package org.zalando.opentracing.spring.webflux.extension;

import io.opentracing.mock.MockSpan;
import io.opentracing.mock.MockTracer;
import static org.assertj.core.api.Assertions.*;
import org.junit.jupiter.api.Test;
import static org.mockito.Mockito.*;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.web.server.ServerWebExchange;

class ErrorSpanDecoratorTest {

    @Test
    void onTagNullStatusCode() {
        ServerWebExchange exchange = mock(ServerWebExchange.class);
        when(exchange.getResponse()).thenReturn(mock(ServerHttpResponse.class));

        MockSpan span = new MockTracer().buildSpan("span").start();
        new ErrorSpanDecorator().onResponse(exchange, span);

        assertThat(span.tags().get("error")).isEqualTo(true);
    }
}
