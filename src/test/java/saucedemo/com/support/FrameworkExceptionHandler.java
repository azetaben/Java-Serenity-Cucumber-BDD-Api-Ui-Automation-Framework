package saucedemo.com.support;

import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriverException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.SocketTimeoutException;
import java.net.http.HttpTimeoutException;
import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.Set;

public final class FrameworkExceptionHandler {
    private static final Logger log = LoggerFactory.getLogger(FrameworkExceptionHandler.class);

    private FrameworkExceptionHandler() {
    }

    public static Category classify(Throwable failure) {
        if (failure == null) {
            throw new IllegalArgumentException("Failure must not be null");
        }

        Set<Throwable> visited = Collections.newSetFromMap(new IdentityHashMap<>());
        Category category = Category.UNEXPECTED;
        for (Throwable cause = failure; cause != null && visited.add(cause); cause = cause.getCause()) {
            Category causeCategory = classifySingle(cause);
            if (priority(causeCategory) < priority(category)) {
                category = causeCategory;
            }
        }
        return category;
    }

    public static void handle(String context, Throwable failure) {
        if (context == null || context.isBlank()) {
            throw new IllegalArgumentException("Failure context must not be blank");
        }
        if (failure == null) {
            log.error("{} failed without an exception being provided", context);
            return;
        }

        log.error("{} failed [{}]: {}", context, classify(failure), failure.getMessage(), failure);
    }

    private static Category classifySingle(Throwable failure) {
        if (failure instanceof AssertionError) return Category.ASSERTION;
        if (failure instanceof TimeoutException
                || failure instanceof java.util.concurrent.TimeoutException
                || failure instanceof SocketTimeoutException
                || failure instanceof HttpTimeoutException) {
            return Category.TIMEOUT;
        }
        if (failure instanceof WebDriverException) return Category.BROWSER_AUTOMATION;
        if (failure instanceof IOException) return Category.NETWORK_OR_IO;
        if (failure instanceof IllegalArgumentException) return Category.INVALID_INPUT_OR_CONFIGURATION;
        if (failure instanceof IllegalStateException) return Category.FRAMEWORK_STATE;
        return Category.UNEXPECTED;
    }

    private static int priority(Category category) {
        return switch (category) {
            case ASSERTION -> 0;
            case TIMEOUT -> 1;
            case BROWSER_AUTOMATION -> 2;
            case NETWORK_OR_IO -> 3;
            case INVALID_INPUT_OR_CONFIGURATION -> 4;
            case FRAMEWORK_STATE -> 5;
            case UNEXPECTED -> 6;
        };
    }

    public enum Category {
        ASSERTION,
        TIMEOUT,
        BROWSER_AUTOMATION,
        NETWORK_OR_IO,
        INVALID_INPUT_OR_CONFIGURATION,
        FRAMEWORK_STATE,
        UNEXPECTED
    }
}
