package org.zalando.opentracing.spring.webflux.extension;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import static org.springframework.http.HttpStatus.SERVICE_UNAVAILABLE;
import static org.springframework.http.MediaType.TEXT_PLAIN_VALUE;

@RestController
@RequestMapping(produces = TEXT_PLAIN_VALUE)
public class ExampleController {

    @RequestMapping(path = "/greetings/{name}")
    public Mono<ResponseEntity<String>> greet(@PathVariable final String name) {
        return Mono.just(ResponseEntity.ok()
                .header("Retry-After", "60")
                .body("Hello, " + name + "!"));
    }

    @GetMapping("/error")
    @ResponseStatus(SERVICE_UNAVAILABLE)
    public Mono<Void> error() {
        return Mono.empty();
    }

}
