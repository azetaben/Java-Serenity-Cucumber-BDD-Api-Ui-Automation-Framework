package saucedemo.com.assertions;

import saucedemo.com.support.FrameworkLogger;
import java.math.BigDecimal;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.assertj.core.api.Assertions.assertThat;

public final class MoneyAssertions {
    private static final Pattern DOLLAR_AMOUNT = Pattern.compile(
            "^(?:[^$\\r\\n]*:)?\\s*\\$(-?(?:\\d{1,3}(?:,\\d{3})+|\\d+)(?:\\.\\d{1,2})?)\\s*$");

    private MoneyAssertions() {
    }

    public static BigDecimal parseDollarAmount(String displayedText) {
        if (displayedText == null) throw new IllegalArgumentException("Dollar amount must not be null");
        Matcher matcher = DOLLAR_AMOUNT.matcher(displayedText.trim());
        if (!matcher.matches()) {
            throw new IllegalArgumentException("Expected a dollar amount, but was: " + displayedText);
        }
        return new BigDecimal(matcher.group(1).replace(",", ""));
    }

    public static void assertAmountsEqual(String message, BigDecimal expected, BigDecimal actual) {
        FrameworkLogger.assertionStarted(MoneyAssertions.class, "assertAmountsEqual");
        assertThat(expected.compareTo(actual)).as(message + ": expected " + expected + " but was " + actual).isEqualTo(0);
    }

}
