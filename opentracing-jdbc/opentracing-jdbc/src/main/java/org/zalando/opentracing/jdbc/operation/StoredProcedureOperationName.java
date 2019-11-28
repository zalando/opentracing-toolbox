package org.zalando.opentracing.jdbc.operation;

import lombok.AllArgsConstructor;
import org.apiguardian.api.API;

import java.lang.reflect.Method;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.apiguardian.api.API.Status.EXPERIMENTAL;

/**
 * @see <a href="https://github.com/zalando-stups/java-sproc-wrapper/blob/b86a6243e83e8ea33d1a46e9dbac8c79082dc0ec/src/main/java/de/zalando/sprocwrapper/proxy/StoredProcedure.java#L262-L264">zalando-stups/java-sproc-wrapper</a>
 */
@API(status = EXPERIMENTAL)
@AllArgsConstructor
public final class StoredProcedureOperationName implements OperationName {

    private final Pattern pattern = Pattern.compile("^SELECT \\* FROM (.+)\\(.*\\)$");

    private final OperationName fallback;

    public StoredProcedureOperationName() {
        this(new DefaultOperationName());
    }

    @Override
    public String generate(final Method method, final List<String> queries) {
        for (final String query : queries) {
            final Matcher matcher = pattern.matcher(query);

            if (matcher.matches()) {
                return matcher.group(1);
            }
        }

        return fallback.generate(method, queries);
    }

}
