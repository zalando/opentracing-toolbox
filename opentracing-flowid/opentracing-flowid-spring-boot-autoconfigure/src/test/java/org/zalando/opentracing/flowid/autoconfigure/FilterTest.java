package org.zalando.opentracing.flowid.autoconfigure;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = Application.class)
@ImportAutoConfiguration(FlowIdAutoConfiguration.class)
class FilterTest {

    @Autowired
    @Qualifier("flowFilter")
    private FilterRegistrationBean flowFilter;

    @Test
    void shouldInitializeFilter() {
        assertThat(flowFilter, is(notNullValue()));
    }
}
