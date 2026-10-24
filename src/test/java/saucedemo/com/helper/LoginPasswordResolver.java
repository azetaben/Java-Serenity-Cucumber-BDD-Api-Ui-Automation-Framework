package saucedemo.com.helper;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Locale;
import java.util.function.Supplier;

/**
 * Resolves named password modes without placing credential values in Gherkin.
 */
public final class LoginPasswordResolver {
    private static final Logger log = LoggerFactory.getLogger(LoginPasswordResolver.class);

    private LoginPasswordResolver() {
    }

    public static String resolve(String mode, Supplier<String> validPassword) {
        if (mode == null || mode.isBlank()) {
            throw new IllegalArgumentException("Password mode must not be blank");
        }
        log.debug("Resolving login password mode '{}'", mode.trim().toLowerCase(Locale.ROOT));
        return switch (mode.trim().toLowerCase(Locale.ROOT)) {
            case "valid" -> {
                String password = validPassword.get();
                if (password == null || password.isBlank()) {
                    throw new IllegalStateException("Configured valid password must not be blank");
                }
                yield password;
            }
            case "invalid" -> "invalid-password";
            case "empty" -> "";
            default -> throw new IllegalArgumentException("Unknown password mode: " + mode);
        };
    }
}
