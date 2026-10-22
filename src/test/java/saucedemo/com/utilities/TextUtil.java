package saucedemo.com.utilities;

/**
 * Text normalisation only. Monetary parsing belongs to MoneyAssertions.
 */
public final class TextUtil {
    private TextUtil() {
    }

    public static String nullToBlank(String value) {
        return value == null ? "" : value;
    }

    public static String normalizeWhitespace(String value) {
        return nullToBlank(value).replace('\u00a0', ' ').replaceAll("\\s+", " ").trim();
    }
}
