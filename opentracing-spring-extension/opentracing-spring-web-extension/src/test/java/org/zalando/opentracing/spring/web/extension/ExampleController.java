package org.zalando.opentracing.spring.web.extension;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.CompletableFuture;

import static org.springframework.http.HttpStatus.SERVICE_UNAVAILABLE;
import static org.springframework.http.MediaType.TEXT_PLAIN_VALUE;
import static org.springframework.web.bind.annotation.RequestMethod.GET;

@RestController
@RequestMapping(produces = TEXT_PLAIN_VALUE)
public class ExampleController {

    @GetMapping
    public String hello() {
        return "Hello";
    }

    @RequestMapping(method = GET, path = "/greet")
    public String greet(@RequestParam final String name) {
        return "Hello, " + name + "!";
    }

    @GetMapping("/names/{id}")
    public String getName(@PathVariable final String id) {
        return "Bob";
    }

    @GetMapping("/async")
    public CompletableFuture<String> async() {
        return CompletableFuture.completedFuture("Later");
    }

    @GetMapping("/error")
    @ResponseStatus(SERVICE_UNAVAILABLE)
    public void error() {
        // nothing to do
    }

    @GetMapping("/exception")
    public void exception() {
        throw new UnsupportedOperationException("Error");
    }

}
