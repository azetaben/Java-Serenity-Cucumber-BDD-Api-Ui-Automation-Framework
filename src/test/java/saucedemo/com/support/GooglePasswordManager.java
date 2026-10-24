package saucedemo.com.support;

import net.thucydides.core.webdriver.WebDriverFacade;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

/**
 * Native password-prompt handling for local, visible Chrome on Windows.
 */
public final class GooglePasswordManager {
    private static final Logger log = LoggerFactory.getLogger(GooglePasswordManager.class);

    private GooglePasswordManager() {
    }

    public static void waitUntilVisible(WebDriver driver, int timeoutSeconds) {
        if (timeoutSeconds < 1 || timeoutSeconds > 120) {
            throw new IllegalArgumentException("Password Manager timeout must be between 1 and 120 seconds");
        }
        Long pid = chromeProcessId(driver);
        if (pid == null) {
            throw new IllegalStateException("Native Password Manager detection requires local Chrome on Windows with goog:processID");
        }
        int result = runDetector(pid, timeoutSeconds, false);
        if (result == 2) {
            throw new AssertionError("Google Password Manager did not appear within " + timeoutSeconds + " seconds");
        }
        requireSuccess(result);
    }

    /**
     * No-op when no prompt is visible or the browser is not local Windows Chrome.
     */
    public static void dismissIfVisible(WebDriver driver) {
        Long pid = chromeProcessId(driver);
        if (pid == null) {
            log.debug("Native Password Manager dismissal is unavailable for this browser/provider");
            return;
        }
        int result = runDetector(pid, 3, true);
        if (result == 2) {
            log.debug("No native Password Manager prompt is visible");
            return;
        }
        requireSuccess(result);
        log.info("Dismissed the native Google Password Manager prompt");
    }

    private static Long chromeProcessId(WebDriver driver) {
        if (!System.getProperty("os.name", "").toLowerCase(Locale.ROOT).startsWith("windows")) return null;
        WebDriver actual = driver instanceof WebDriverFacade facade ? facade.getProxiedDriver() : driver;
        if (!(actual instanceof ChromeDriver chrome)) return null;
        Object value = chrome.getCapabilities().getCapability("goog:processID");
        return value instanceof Number pid && pid.longValue() > 0 ? pid.longValue() : null;
    }

    private static void requireSuccess(int result) {
        if (result == 4) {
            throw new IllegalStateException("Password Manager appeared but its dismissal could not be confirmed");
        }
        if (result != 0) {
            throw new IllegalStateException("Windows UI Automation could not inspect the Chrome password prompt");
        }
    }

    private static int runDetector(long pid, int timeoutSeconds, boolean dismiss) {
        Process process = null;
        try (var resource = GooglePasswordManager.class.getResourceAsStream("/native/wait-password-manager.ps1")) {
            if (resource == null) throw new IllegalStateException("Missing native Password Manager script");
            String script = new String(resource.readAllBytes(), StandardCharsets.UTF_8)
                    .replace("__CHROME_PID__", Long.toString(pid))
                    .replace("__TIMEOUT_SECONDS__", Integer.toString(timeoutSeconds))
                    .replace("__DISMISS__", Boolean.toString(dismiss));
            String encoded = Base64.getEncoder().encodeToString(script.getBytes(StandardCharsets.UTF_16LE));
            process = new ProcessBuilder("powershell.exe", "-NoProfile", "-NonInteractive", "-EncodedCommand", encoded)
                    .redirectOutput(ProcessBuilder.Redirect.DISCARD)
                    .redirectError(ProcessBuilder.Redirect.DISCARD).start();
            if (!process.waitFor(timeoutSeconds + 10L, TimeUnit.SECONDS)) {
                throw new IllegalStateException("Native Password Manager detector exceeded its deadline");
            }
            return process.exitValue();
        } catch (IOException exception) {
            throw new java.io.UncheckedIOException("Could not run the native Password Manager detector", exception);
        } catch (InterruptedException exception) {
            Thread.currentThread().interrupt();
            throw new IllegalStateException("Interrupted during native Password Manager handling", exception);
        } finally {
            if (process != null && process.isAlive()) process.destroyForcibly();
        }
    }
}
