package saucedemo.com.support;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ReportSecretScannerTest {
    @TempDir
    Path reportDirectory;

    @Test
    void findsConfiguredSecretsInNestedReportFiles() throws Exception {
        Path json = reportDirectory.resolve("nested/scenario.json");
        Files.createDirectories(json.getParent());
        Files.writeString(json, "{\"description\":\"password: ci-secret\"}");
        Files.writeString(reportDirectory.resolve("safe.xml"), "<testsuite name=\"passed\"/>");

        assertThat(ReportSecretScanner.findLeakingFiles(List.of(reportDirectory), List.of("ci-secret"))).isEqualTo(List.of(json));
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

        assertThat(new HashSet<>(ReportSecretScanner.findLeakingFiles(
                List.of(reportDirectory), List.of("", "secret_sauce")))).isEqualTo(Set.of(html, json, xml, text));
    }

    @Test
    void scanFailsClosedForMissingReportDirectories() {
        assertThatThrownBy(() -> ReportSecretScanner.findLeakingFiles(
                List.of(reportDirectory.resolve("missing")), List.of("secret_sauce"))).isInstanceOf(java.io.IOException.class);
    }
}
