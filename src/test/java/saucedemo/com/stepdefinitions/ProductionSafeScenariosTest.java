package saucedemo.com.stepdefinitions;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

class ProductionSafeScenariosTest {
    private static final String INVENTORY = "classpath:features/ui_tests/inventory/inventory.feature";

    @Test
    void allowsOnlyReviewedScenariosWithProductionSafeTag() {
        assertTrue(ProductionSafeScenarios.isAllowed(INVENTORY, "Verify the product titles", Set.of("@UI", "@ProductionSafe")));
        assertFalse(ProductionSafeScenarios.isAllowed(INVENTORY, "Add and remove a product", Set.of("@ProductionSafe")));
        assertFalse(ProductionSafeScenarios.isAllowed(INVENTORY, "Verify the product titles", Set.of("@UI")));
    }

    @Test
    void rejectsDuplicateNamesFromUnreviewedFeatures() {
        assertFalse(ProductionSafeScenarios.isAllowed("features/ui_tests/other/inventory.feature",
                "Verify the product titles", Set.of("@ProductionSafe")));
    }

    @Test
    void acceptsCucumberClasspathUris() {
        assertTrue(ProductionSafeScenarios.isAllowed(
                URI.create("classpath:features/ui_tests/inventory/inventory.feature"),
                "Verify the product titles", Set.of("@ProductionSafe")));
    }

    @Test
    void exposesTheReviewedAllowlistForGovernance() {
        assertEquals(Set.of("Verify the product titles", "Verify product prices and images", "Open a product"),
                ProductionSafeScenarios.reviewedAllowlist().get("features/ui_tests/inventory/inventory.feature"));
        assertEquals(2, ProductionSafeScenarios.reviewedAllowlist().size());
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
        assertEquals(ProductionSafeScenarios.reviewedAllowlist(), taggedFeatures,
                "Update the production allowlist and this test together when @ProductionSafe coverage changes");
    }
}
