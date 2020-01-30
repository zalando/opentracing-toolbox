package org.zalando.opentracing.proxy;

import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.importer.ClassFileImporter;
import com.tngtech.archunit.core.importer.ImportOption.DoNotIncludeTests;
import com.tngtech.archunit.lang.ArchRule;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.Arrays;
import java.util.List;

import static com.tngtech.archunit.library.Architectures.layeredArchitecture;
import static com.tngtech.archunit.library.dependencies.SlicesRuleDefinition.slices;

public class ArchitectureTest {

    private final JavaClasses importedClasses = new ClassFileImporter()
            .withImportOption(new DoNotIncludeTests())
            .importPackages("org.zalando.opentracing.proxy");

    static List<ArchRule> rules() {
        return Arrays.asList(
                layeredArchitecture()
                        .layer("base").definedBy("org.zalando.opentracing.proxy.base..")
                        .layer("core").definedBy("org.zalando.opentracing.proxy.core..")
                        .layer("interceptors").definedBy("org.zalando.opentracing.proxy.intercept..")
                        .layer("listeners").definedBy("org.zalando.opentracing.proxy.listen..")
                        .layer("plugins").definedBy("org.zalando.opentracing.proxy.plugin..")
                        .layer("spi").definedBy("org.zalando.opentracing.proxy.spi..")
                        .whereLayer("core").mayNotBeAccessedByAnyLayer()
                        .whereLayer("interceptors").mayOnlyBeAccessedByLayers("spi", "core", "plugins")
                        .whereLayer("listeners").mayOnlyBeAccessedByLayers("spi", "core", "interceptors", "plugins")
                        .whereLayer("plugins").mayNotBeAccessedByAnyLayer()
                        .whereLayer("spi").mayOnlyBeAccessedByLayers("core", "interceptors", "listeners", "plugins"),

                slices().matching("org.zalando.opentracing.proxy.(**)")
                        .should().beFreeOfCycles()
        );
    }

    @ParameterizedTest
    @MethodSource("rules")
    void test(final ArchRule rule) {
        rule.check(importedClasses);
    }

}
