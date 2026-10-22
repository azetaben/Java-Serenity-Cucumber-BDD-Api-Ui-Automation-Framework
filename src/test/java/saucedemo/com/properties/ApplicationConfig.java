package saucedemo.com.properties;

import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.function.Function;

public final class ApplicationConfig {
    private static final Logger log = LoggerFactory.getLogger(ApplicationConfig.class);
    private static final Config CONFIG = ConfigFactory.load()
            .withFallback(ConfigFactory.parseResources("serenity.conf"))
            .resolve();

    static {
        log.info("Framework configuration loaded; environment overrides are enabled");
    }

    private ApplicationConfig() {
    }

    public static String required(String key) {
        return required(key, System::getenv);
    }

    static String required(String key, Function<String, String> environmentLookup) {
        String environmentKey = switch (key) {
            case "standard_user.username" -> "SAUCEDEMO_TEST_USERNAME";
            case "all_users.password" -> "SAUCEDEMO_TEST_PASSWORD";
            default -> null;
        };
        String override = environmentKey == null ? null : environmentLookup.apply(environmentKey);
        if (override != null && !override.isBlank()) {
            log.debug("Resolved configuration key '{}' from its environment override", key);
            return override;
        }
        if (CONFIG.hasPath(key)) {
            log.debug("Resolved configuration key '{}' from application configuration", key);
            return CONFIG.getString(key);
        }
        String configuredEnvironmentKey = "environments.default." + key;
        if (CONFIG.hasPath(configuredEnvironmentKey)) {
            log.debug("Resolved configuration key '{}' from the default environment", key);
            return CONFIG.getString(configuredEnvironmentKey);
        }
        log.error("Required configuration key '{}' is missing", key);
        throw new IllegalArgumentException("Missing configuration property: " + key);
    }

    public static int integer(String key) {
        return CONFIG.getInt(key);
    }

    public static String value(String key, String fallback) {
        return CONFIG.hasPath(key) ? CONFIG.getString(key) : fallback;
    }
}
