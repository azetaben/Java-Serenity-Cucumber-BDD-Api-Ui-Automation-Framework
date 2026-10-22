package saucedemo.com.support;

import net.thucydides.core.scheduling.NormalFluentWait;
import net.thucydides.core.webdriver.WebDriverFacade;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WrapsDriver;
import org.openqa.selenium.chromium.HasCdp;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import saucedemo.com.properties.ApplicationConfig;

import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Duration;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public final class PdfDownloads {
    private static final Logger log = LoggerFactory.getLogger(PdfDownloads.class);

    private PdfDownloads() {
    }

    public static Path directory() {
        return Paths.get(System.getProperty("user.dir"))
                .resolve(ApplicationConfig.required("framework.paths.download"))
                .resolve(org.slf4j.MDC.get("downloadId") == null ? "manual-" + ProcessHandle.current().pid() : org.slf4j.MDC.get("downloadId"))
                .toAbsolutePath()
                .normalize();
    }

    public static Set<Path> snapshot() {
        try {
            Files.createDirectories(directory());
            return new HashSet<>(files(directory()));
        } catch (IOException e) {
            throw new UncheckedIOException(
                    "Cannot prepare PDF download directory", e
            );
        }
    }

    /**
     * Use the same absolute directory for Chrome and the file-system wait.
     */
    public static void prepareBrowserDownload(WebDriver driver) {
        try {
            Files.createDirectories(directory());
        } catch (IOException exception) {
            throw new UncheckedIOException("Cannot prepare download directory " + directory(), exception);
        }
        WebDriver browser = driver;
        if (browser instanceof WebDriverFacade) {
            browser = ((WebDriverFacade) browser).getProxiedDriver();
        }
        while (browser instanceof WrapsDriver) {
            WebDriver wrapped = ((WrapsDriver) browser).getWrappedDriver();
            if (wrapped == browser) break;
            browser = wrapped;
        }
        if (browser instanceof HasCdp) {
            Map<String, Object> parameters = new HashMap<>();
            parameters.put("behavior", "allow");
            parameters.put("downloadPath", directory().toString());
            ((HasCdp) browser).executeCdpCommand("Browser.setDownloadBehavior", parameters);
        }
    }

    public static Map<Path, String> snapshotState() {
        snapshot();
        Map<Path, String> result = new HashMap<>();
        try {
            for (Path path : files(directory())) result.put(path, stamp(path));
        } catch (IOException exception) {
            throw new UncheckedIOException("Cannot snapshot " + directory(), exception);
        }
        return result;
    }

    public static Path waitForNewPdf(Set<Path> existingFiles, Duration timeout) {
        Map<Path, String> existing = new HashMap<>();
        for (Path path : existingFiles) existing.put(path, null);
        return waitForNewOrUpdatedPdf(existing, timeout);
    }

    public static Path waitForNewOrUpdatedPdf(Map<Path, String> existingFiles, Duration timeout) {
        if (timeout == null || timeout.isNegative() || timeout.isZero()) {
            throw new IllegalArgumentException("PDF download timeout must be positive");
        }
        Map<Path, String> previousStates = new HashMap<>();
        String pdfExtension = ApplicationConfig.required("framework.pdf.extension").toLowerCase(Locale.ROOT);
        String inProgressExtension = ApplicationConfig.required("framework.pdf.in.progress.extension").toLowerCase(Locale.ROOT);
        Path folder = directory();
        log.info("Waiting for a completed PDF in {}", folder);
        java.util.concurrent.atomic.AtomicReference<Path> completed = new java.util.concurrent.atomic.AtomicReference<>();
        new NormalFluentWait<>(folder)
                .withTimeout(timeout)
                .pollingEvery(ApplicationConfig.integer("framework.pdf.download.poll.millis"), TimeUnit.MILLISECONDS)
                .withMessage(() -> "No completed new or updated PDF appeared in " + folder
                        + "; observed files: " + describeFiles(folder))
                .until(downloadFolder -> {
                    try {
                        List<Path> currentFiles = files(downloadFolder);
                        boolean downloading = currentFiles.stream()
                                .filter(path -> !existingFiles.containsKey(path)
                                        || !Objects.equals(existingFiles.get(path), stateOrNull(path)))
                                .anyMatch(path -> filename(path).endsWith(inProgressExtension));
                        if (downloading) {
                            previousStates.clear();
                            return false;
                        }
                        for (Path file : currentFiles) {
                            if (!filename(file).endsWith(pdfExtension)) continue;
                            String state = stamp(file);
                            if (existingFiles.containsKey(file) && (existingFiles.get(file) == null
                                    || existingFiles.get(file).equals(state))) continue;
                            String previous = previousStates.put(file, state);
                            if (Files.size(file) > 0 && state.equals(previous) && hasPdfSignature(file)) {
                                log.info("Verified completed PDF {} ({} bytes)", file.getFileName(), Files.size(file));
                                completed.set(file);
                                return true;
                            }
                        }
                    } catch (IOException exception) {
                        // Retry while Chrome creates, writes or renames the file.
                    }
                    return false;
                });
        return completed.get();
    }

    private static String stamp(Path file) throws IOException {
        return Files.getLastModifiedTime(file) + ":" + Files.size(file);
    }

    private static String stateOrNull(Path file) {
        try {
            return stamp(file);
        } catch (IOException exception) {
            return null;
        }
    }

    private static String describeFiles(Path folder) {
        try {
            return files(folder).stream().map(path -> filename(path) + " (" + stateOrNull(path) + ")")
                    .collect(Collectors.joining(", "));
        } catch (IOException exception) {
            return exception.toString();
        }
    }

    private static List<Path> files(Path directory) throws IOException {
        try (Stream<Path> paths = Files.list(directory)) {
            return paths.filter(Files::isRegularFile)
                    .collect(Collectors.toList());
        }
    }

    private static String filename(Path path) {
        return path.getFileName().toString()
                .toLowerCase(Locale.ROOT);
    }

    private static boolean hasPdfSignature(Path file) throws IOException {
        String signature = ApplicationConfig.required("framework.pdf.signature");

        try (InputStream input = Files.newInputStream(file)) {
            return hasPdfSignature(input, signature);
        }
    }

    /**
     * Reads the complete header even when a stream returns fewer bytes per read.
     */
    static boolean hasPdfSignature(InputStream input, String signature) throws IOException {
        if (signature.isEmpty()) {
            throw new IllegalArgumentException("PDF signature must not be empty");
        }
        byte[] expected = signature.getBytes(StandardCharsets.US_ASCII);
        byte[] header = new byte[expected.length];
        int offset = 0;
        while (offset < header.length) {
            int count = input.read(header, offset, header.length - offset);
            if (count < 0) return false;
            if (count == 0) {
                int nextByte = input.read();
                if (nextByte < 0) return false;
                header[offset++] = (byte) nextByte;
            } else {
                offset += count;
            }
        }
        return Arrays.equals(expected, header);
    }
}
