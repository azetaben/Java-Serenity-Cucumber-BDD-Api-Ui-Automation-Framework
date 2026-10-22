package saucedemo.com.support;

import net.thucydides.core.webdriver.WebDriverFacade;
import org.openqa.selenium.HasDownloads;
import org.openqa.selenium.WebDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Path;
import java.time.Duration;

/**
 * Selenium Grid adapter. Providers without managed downloads must use their own supported API.
 */
public final class ManagedPDFDownloads {
    private static final Logger log = LoggerFactory.getLogger(ManagedPDFDownloads.class);

    private ManagedPDFDownloads() {
    }

    public static HasDownloads enabled(WebDriver driver) {
        WebDriver actual = driver instanceof WebDriverFacade facade ? facade.getProxiedDriver() : driver;
        if (actual instanceof HasDownloads downloads && downloads.isDownloadsEnabled()) return downloads;
        log.error("Remote WebDriver does not expose managed downloads");
        throw new IllegalStateException("Remote PDF retrieval requires se:downloadsEnabled and a managed-downloads Grid node");
    }

    public static Path awaitPdf(WebDriver driver, Path directory, Duration timeout) {
        log.info("Waiting for a remotely managed PDF download");
        HasDownloads downloads = enabled(driver);
        String name = new org.openqa.selenium.support.ui.FluentWait<>(downloads).withTimeout(timeout)
                .pollingEvery(Duration.ofMillis(250)).until(current -> current.getDownloadedFiles().stream().map(HasDownloads.DownloadedFile::getName)
                        .filter(file -> file.toLowerCase(java.util.Locale.ROOT).endsWith(".pdf")).findFirst().orElse(null));
        try {
            java.nio.file.Files.createDirectories(directory);
            Path file = directory.toAbsolutePath().normalize().resolve(name).normalize();
            if (!file.startsWith(directory.toAbsolutePath().normalize()))
                throw new IllegalStateException("Unexpected download filename");
            downloads.downloadFile(name, directory);
            log.info("Retrieved remote PDF file '{}'", name);
            return file;
        } catch (java.io.IOException exception) {
            log.error("Could not prepare remote PDF download directory", exception);
            throw new java.io.UncheckedIOException(exception);
        }
    }
}
