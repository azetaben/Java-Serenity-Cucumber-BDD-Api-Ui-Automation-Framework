package saucedemo.com.stepdefinitions;

import java.net.URI;
import java.util.Collection;
import java.util.Map;
import java.util.Set;

/**
 * Reviewed allowlist for production UI coverage. Update with feature tag changes.
 */
public final class ProductionSafeScenarios {
    private static final Map<String, Set<String>> ALLOWED = Map.of(
            "features/ui_tests/inventory/inventory.feature", Set.of(
                    "Verify the product titles",
                    "Verify product prices and images",
                    "Open a product"),
            "features/ui_tests/sorting/sorting.feature", Set.of(
                    "Standard account sorts the complete catalog"));

    private ProductionSafeScenarios() {
    }

    public static boolean isAllowed(String featurePath, String scenarioName, Collection<String> tags) {
        if (featurePath == null || scenarioName == null || tags == null
                || tags.stream().noneMatch(tag -> "@ProductionSafe".equalsIgnoreCase(tag.startsWith("@")
                ? tag : "@" + tag))) {
            return false;
        }
        String normalized = featurePath.replace('\\', '/');
        return ALLOWED.entrySet().stream()
                .filter(entry -> normalized.endsWith(entry.getKey()))
                .anyMatch(entry -> entry.getValue().contains(scenarioName));
    }

    public static boolean isAllowed(URI featureUri, String scenarioName, Collection<String> tags) {
        if (featureUri == null) return false;
        String path = featureUri.getPath() == null ? featureUri.getSchemeSpecificPart() : featureUri.getPath();
        return isAllowed(path, scenarioName, tags);
    }

    public static Map<String, Set<String>> reviewedAllowlist() {
        return ALLOWED;
    }
}
