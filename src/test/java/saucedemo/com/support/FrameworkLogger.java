package saucedemo.com.support;

import org.slf4j.LoggerFactory;

/** Shared, parameter-free logging for framework execution boundaries. */
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

    private static void log(Class<?> component, String layer, String methodName) {
        int sourceLine = StackWalker.getInstance(StackWalker.Option.RETAIN_CLASS_REFERENCE)
                .walk(frames -> frames
                        .filter(frame -> frame.getDeclaringClass() == component)
                        .mapToInt(StackWalker.StackFrame::getLineNumber)
                        .findFirst()
                        .orElse(-1));
        LoggerFactory.getLogger(component).info("Page: {} - {} started: {}",
                sourceLine > 0 ? sourceLine : "n/a", layer, methodName);
    }
}
