package saucedemo.com.assertions;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import saucedemo.com.support.FrameworkLogger;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;


public final class CollectionAssertions {
    private CollectionAssertions() {
    }

    private static <T> void ordered(List<T> actual, Comparator<? super T> comparator, boolean descending, String label) {
        FrameworkLogger.assertionStarted(CollectionAssertions.class, "ordered");
        assertFalse(actual.isEmpty(), label + ": expected at least one item");
        var expected = new ArrayList<>(actual);
        expected.sort(comparator);
        if (descending) Collections.reverse(expected);
        assertEquals(expected, actual, label);
    }

    /**
     * Direction is interpreted here; callers always supply an ascending comparator.
     */
    public static <T> void ordered(List<T> actual, Comparator<? super T> comparator, String direction, String label) {
        if (direction == null || (!direction.equalsIgnoreCase("ascending") && !direction.equalsIgnoreCase("descending"))) {
            throw new IllegalArgumentException("Unknown sort direction: " + direction);
        }
        ordered(actual, comparator, direction.equalsIgnoreCase("descending"), label);
    }

    public static void sameTitles(List<String> expected, List<String> actual) {
        FrameworkLogger.assertionStarted(CollectionAssertions.class, "sameTitles");
        var a = new ArrayList<>(expected);
        var b = new ArrayList<>(actual);
        Collections.sort(a);
        Collections.sort(b);
        assertEquals(a, b, "Product titles");
    }
}
