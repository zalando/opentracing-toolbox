package org.zalando.opentracing.spring.webflux.extension.autoconfigure;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import static org.springframework.http.MediaType.TEXT_PLAIN_VALUE;

@RestController
@RequestMapping(produces = TEXT_PLAIN_VALUE)
public class ExampleController {

    @RequestMapping(path = "/greetings/{name}")
    public Mono<String> greet(@PathVariable final String name) {
        return Mono.just("Hello, " + name + "!");
    }

}
