package org.zalando.opentracing.proxy.autoconfigure;

import com.google.common.base.CaseFormat;
import lombok.Getter;
import lombok.Setter;
import org.apiguardian.api.API;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.List;

import static com.google.common.collect.Lists.newArrayList;
import static org.apiguardian.api.API.Status.INTERNAL;

@API(status = INTERNAL)
@Getter
@Component
@ConfigurationProperties(prefix = "opentracing.proxy")
public final class ProxyProperties {

    private final AutoTagging autoTagging = new AutoTagging();
    private final LogCorrelation logCorrelation = new LogCorrelation();
    private final Rename rename = new Rename();
    private final TagPropagation tagPropagation = new TagPropagation();

    @Getter
    public static final class AutoTagging {
        private final List<String> keys = newArrayList("flow_id");
    }

    @Getter
    @Setter
    public static final class LogCorrelation {
        private String traceId = "trace_id";
        private String spanId = "span_id";
        private final List<String> baggage = newArrayList("flow_id");
    }

    @Getter
    @Setter
    public static final class Rename {
        private CaseFormat format = CaseFormat.LOWER_UNDERSCORE;
    }

    @Getter
    public static final class TagPropagation {
        private final List<String> keys = newArrayList("flow_id");
    }

}
