package saucedemo.com.support;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Shared, parameter-free logging for framework execution boundaries.
 */
public final class FrameworkLogger {
    private FrameworkLogger() {
    }

    public static void actionStarted(Class<?> component, String methodName) {
        log(component, "Action", methodName);
    }

    public static void stepDefinitionStarted(Class<?> component, String methodName) {
        log(component, "Cucumber step definition", methodName);
    }

    public static void pageOperationStarted(Class<?> component, String methodName) {
        log(component, "Page operation", methodName);
    }

    public static void assertionStarted(Class<?> component, String methodName) {
        log(component, "Assertion", methodName);
    }

    public static void actionEnded(Class<?> component, String methodName) {
        log(LoggerFactory.getLogger(component), component, "Action", "ended", methodName);
    }

    /** Emit through the action's own logger while retaining its source-line context. */
    public static void actionStarted(Logger logger, Class<?> component, String description) {
        log(logger, component, "Action", "started", description);
    }

    public static void pageOperationStarted(Logger logger, Class<?> component, String description) {
        log(logger, component, "Page operation", "started", description);
    }

    public static void actionEnded(Logger logger, Class<?> component, String description) {
        log(logger, component, "Action", "ended", description);
    }

    private static void log(Class<?> component, String layer, String description) {
        log(LoggerFactory.getLogger(component), component, layer, "started", description);
    }

    private static void log(Logger logger, Class<?> component, String layer, String event, String description) {
        int sourceLine = StackWalker.getInstance(StackWalker.Option.RETAIN_CLASS_REFERENCE)
                .walk(frames -> frames
                        .filter(frame -> frame.getDeclaringClass() == component)
                        .mapToInt(StackWalker.StackFrame::getLineNumber)
                        .findFirst()
                        .orElse(-1));
        logger.info("Page: {} - {} {}: {}",
                sourceLine > 0 ? sourceLine : "n/a", layer, event, description);
    }
}