package org.zalando.opentracing.servlet.extension.autoconfigure;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.http.MediaType.TEXT_PLAIN_VALUE;
import static org.springframework.web.bind.annotation.RequestMethod.GET;

@RestController
@RequestMapping(produces = TEXT_PLAIN_VALUE)
public class ExampleController {

    @RequestMapping(method = GET, path = "/greet")
    public String greet(@RequestParam final String name) {
        return "Hello, " + name + "!";
    }

}
