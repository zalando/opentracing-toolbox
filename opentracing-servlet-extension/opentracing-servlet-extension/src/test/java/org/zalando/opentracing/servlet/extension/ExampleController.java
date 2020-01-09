package org.zalando.opentracing.servlet.extension;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.async.AsyncRequestTimeoutException;

import java.util.concurrent.Callable;

import static org.springframework.http.HttpStatus.SERVICE_UNAVAILABLE;
import static org.springframework.http.MediaType.TEXT_PLAIN_VALUE;

@RestController
@RequestMapping(produces = TEXT_PLAIN_VALUE)
public class ExampleController {

    @GetMapping
    public String hello() {
        return "Hello";
    }

    @RequestMapping(path = "/greet")
    public ResponseEntity<String> greet(@RequestParam final String name) {
        return ResponseEntity.ok()
                .header("Retry-After", "60")
                .body("Hello, " + name + "!");
    }

    @GetMapping("/names/{id}")
    public String getName(@PathVariable final String id) {
        return "Bob";
    }

    @GetMapping("/async")
    public Callable<String> async() {
        return () -> {
            try {
                Thread.sleep(5_000);
                return "Later";
            } catch (final InterruptedException e) {
                return "Interrupted";
            }
        };
    }

    @ExceptionHandler(AsyncRequestTimeoutException.class)
    public String onTimeout() {
        return "Interrupted";
    }

    @GetMapping("/error")
    @ResponseStatus(SERVICE_UNAVAILABLE)
    public void error() {
        // nothing to do here
    }

    @GetMapping("/exception")
    public void exception() {
        throw new UnsupportedOperationException("Error");
    }

}
