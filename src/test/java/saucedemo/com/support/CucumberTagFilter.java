package saucedemo.com.support;

import java.util.Locale;

/** Builds the single tag expression passed through to the Cucumber engine. */
public final class CucumberTagFilter {
    private static final String DEFAULT_TAGS = "@CartPrice";

    private CucumberTagFilter() { }

    public static String effectiveExpression(String requestedTags, String targetEnvironment) {
        String environment = targetEnvironment == null ? "" : targetEnvironment.trim().toLowerCase(Locale.ROOT);
        String expression = requestedTags == null || requestedTags.isBlank()
                ? DEFAULT_TAGS : requestedTags.trim();

        if (environment.equals("production") && !isProductionSafeExpression(expression)) {
            throw new IllegalArgumentException("Production runs must select @ProductionSafe scenarios only.");
        }
        return "(" + expression + ") and not @manual";
    }

    private static boolean isProductionSafeExpression(String expression) {
        String normalized = expression.toLowerCase(Locale.ROOT).replaceAll("\\s+", " ");
        return !normalized.contains(" or ")
                && (normalized.equals("@productionsafe") || normalized.startsWith("@productionsafe and "));
    }

    public static void main(String[] args) {
        if (args.length != 2) {
            System.err.println("Usage: CucumberTagFilter <target-environment> <requested-tags>");
            System.exit(2);
        }
        try {
            System.out.print(effectiveExpression(args[1], args[0]));
        } catch (IllegalArgumentException exception) {
            System.err.println(exception.getMessage());
            System.exit(2);
        }
    }
}
