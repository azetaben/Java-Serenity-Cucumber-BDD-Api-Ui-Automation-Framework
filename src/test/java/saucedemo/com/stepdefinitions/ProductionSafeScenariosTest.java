package saucedemo.com.stepdefinitions;

import org.junit.jupiter.api.Test;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;

class ProductionSafeScenariosTest {
    private static final String INVENTORY = "classpath:features/ui_tests/inventory/inventory.feature";

    @Test
    void allowsOnlyReviewedScenariosWithProductionSafeTag() {
        assertThat(ProductionSafeScenarios.isAllowed(INVENTORY, "Verify the product titles", Set.of("@UI", "@ProductionSafe"))).isTrue();
        assertThat(ProductionSafeScenarios.isAllowed(INVENTORY, "Add and remove a product", Set.of("@ProductionSafe"))).isFalse();
        assertThat(ProductionSafeScenarios.isAllowed(INVENTORY, "Verify the product titles", Set.of("@UI"))).isFalse();
    }

    @Test
    void rejectsDuplicateNamesFromUnreviewedFeatures() {
        assertThat(ProductionSafeScenarios.isAllowed("features/ui_tests/other/inventory.feature",
                "Verify the product titles", Set.of("@ProductionSafe"))).isFalse();
    }

    @Test
    void acceptsCucumberClasspathUris() {
        assertThat(ProductionSafeScenarios.isAllowed(
                URI.create("classpath:features/ui_tests/inventory/inventory.feature"),
                "Verify the product titles", Set.of("@ProductionSafe"))).isTrue();
    }

    @Test
    void exposesTheReviewedAllowlistForGovernance() {
        assertThat(ProductionSafeScenarios.reviewedAllowlist().get("features/ui_tests/inventory/inventory.feature")).isEqualTo(Set.of("Verify the product titles", "Verify product prices and images", "Open a product"));
        assertThat(ProductionSafeScenarios.reviewedAllowlist().size()).isEqualTo(2);
    }

    @Test
    void featureTagsAndScenarioNamesMustMatchTheReviewedAllowlist() throws IOException, URISyntaxException {
        Path featureRoot = Path.of(getClass().getClassLoader().getResource("features").toURI());
        Map<String, Set<String>> taggedFeatures = new HashMap<>();
        try (var paths = Files.walk(featureRoot)) {
            for (Path feature : paths.filter(path -> path.toString().endsWith(".feature")).toList()) {
                List<String> lines = Files.readAllLines(feature);
                Set<String> featureTags = new HashSet<>();
                Set<String> pendingTags = new HashSet<>();
                for (String line : lines) {
                    String trimmed = line.trim();
                    if (trimmed.startsWith("@")) {
                        for (String tag : trimmed.split("\\s+")) pendingTags.add(tag);
                        continue;
                    }
                    if (trimmed.startsWith("Feature:")) {
                        featureTags.addAll(pendingTags);
                        pendingTags.clear();
                        continue;
                    }
                    if (trimmed.startsWith("Scenario:") || trimmed.startsWith("Scenario Outline:")
                            || trimmed.startsWith("Scenario Template:")) {
                        Set<String> scenarioTags = new HashSet<>(featureTags);
                        scenarioTags.addAll(pendingTags);
                        if (scenarioTags.contains("@ProductionSafe")) {
                            String relativeFeature = "features/" + featureRoot.relativize(feature).toString().replace('\\', '/');
                            String scenarioName = trimmed.substring(trimmed.indexOf(':') + 1).trim();
                            taggedFeatures.computeIfAbsent(relativeFeature, ignored -> new HashSet<>()).add(scenarioName);
                        }
                        pendingTags.clear();
                        continue;
                    }
                    if (!trimmed.isEmpty()) pendingTags.clear();
                }
            }
        }
        assertThat(taggedFeatures).as("Update the production allowlist and this test together when @ProductionSafe coverage changes").isEqualTo(ProductionSafeScenarios.reviewedAllowlist());
    }
}
