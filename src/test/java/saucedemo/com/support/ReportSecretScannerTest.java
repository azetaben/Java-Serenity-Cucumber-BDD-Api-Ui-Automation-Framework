package saucedemo.com.support;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class ReportSecretScannerTest {
    @TempDir
    Path reportDirectory;

    @Test
    void findsConfiguredSecretsInNestedReportFiles() throws Exception {
        Path json = reportDirectory.resolve("nested/scenario.json");
        Files.createDirectories(json.getParent());
        Files.writeString(json, "{\"description\":\"password: ci-secret\"}");
        Files.writeString(reportDirectory.resolve("safe.xml"), "<testsuite name=\"passed\"/>");

        assertEquals(List.of(json), ReportSecretScanner.findLeakingFiles(List.of(reportDirectory), List.of("ci-secret")));
    }

    @Test
    void scansSupportedReportFormatsButIgnoresBinaryAndEmptySecrets() throws Exception {
        Path html = reportDirectory.resolve("report.html");
        Path json = reportDirectory.resolve("scenario.json");
        Path xml = reportDirectory.resolve("tests.xml");
        Path text = reportDirectory.resolve("output.txt");
        Path properties = reportDirectory.resolve("run.properties");
        Path binary = reportDirectory.resolve("screenshot.png");
        Files.writeString(html, "Rendered: secret_sauce");
        Files.writeString(json, "{\"password\":\"secret_sauce\"}");
        Files.writeString(xml, "<failure>secret_sauce</failure>");
        Files.writeString(text, "secret_sauce");
        Files.writeString(properties, "cucumber.filter.tags=@Smoke");
        Files.write(binary, "secret_sauce".getBytes());

        assertEquals(Set.of(html, json, xml, text), new HashSet<>(ReportSecretScanner.findLeakingFiles(
                List.of(reportDirectory), List.of("", "secret_sauce"))));
    }

    @Test
    void scanFailsClosedForMissingReportDirectories() {
        assertThrows(java.io.IOException.class, () -> ReportSecretScanner.findLeakingFiles(
                List.of(reportDirectory.resolve("missing")), List.of("secret_sauce")));
    }
}
