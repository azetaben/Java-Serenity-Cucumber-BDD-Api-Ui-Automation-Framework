package saucedemo.com.support;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collection;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.stream.Stream;

/** Scans text report artifacts for configured test credentials without printing secret values. */
public final class ReportSecretScanner {
    private static final Logger log = LoggerFactory.getLogger(ReportSecretScanner.class);
    private static final Set<String> TEXT_REPORT_EXTENSIONS = Set.of(
            ".json", ".html", ".xml", ".txt", ".csv", ".properties", ".log", ".md");

    private ReportSecretScanner() { }

    public static List<Path> findLeakingFiles(Collection<Path> roots, Collection<String> secrets) throws IOException {
        List<String> needles = secrets.stream()
                .filter(secret -> secret != null && !secret.isBlank())
                .distinct()
                .toList();
        if (needles.isEmpty()) return List.of();

        var leaks = new java.util.LinkedHashSet<Path>();
        for (Path root : roots) {
            if (!Files.exists(root)) throw new IOException("Report directory does not exist: " + root);
            try (Stream<Path> files = Files.walk(root)) {
                for (Path file : files.filter(Files::isRegularFile).toList()) {
                    if (!isTextReport(file)) continue;
                    String contents = Files.readString(file, StandardCharsets.UTF_8);
                    if (needles.stream().anyMatch(contents::contains)) leaks.add(file);
                }
            }
        }
        return List.copyOf(leaks);
    }

    private static boolean isTextReport(Path path) {
        String name = path.getFileName().toString().toLowerCase(Locale.ROOT);
        return TEXT_REPORT_EXTENSIONS.stream().anyMatch(name::endsWith);
    }

    public static void main(String[] args) {
        if (args.length == 0) {
            log.error("At least one report directory is required");
            System.exit(2);
        }
        String configuredPassword = System.getenv("SAUCEDEMO_TEST_PASSWORD");
        List<String> secrets = configuredPassword == null
                ? List.of("secret_sauce")
                : List.of(configuredPassword, "secret_sauce");
        try {
            List<Path> leaks = findLeakingFiles(java.util.Arrays.stream(args).map(Path::of).toList(), secrets);
            if (!leaks.isEmpty()) {
                log.error("Report credential scan found sensitive values; do not archive these files:");
                leaks.forEach(path -> log.error("Potential credential found in report file {}", path));
                System.exit(3);
            }
        } catch (IOException exception) {
            log.error("Could not complete the report credential scan", exception);
            System.exit(4);
        }
    }
}
