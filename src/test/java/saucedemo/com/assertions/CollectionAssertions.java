package saucedemo.com.assertions;

import saucedemo.com.support.FrameworkLogger;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public final class CollectionAssertions {
    private CollectionAssertions() {
    }

    private static <T> void ordered(List<T> actual, Comparator<? super T> comparator, boolean descending, String label) {
        FrameworkLogger.assertionStarted(CollectionAssertions.class, "ordered");
        assertThat(actual.isEmpty()).as(label + ": expected at least one item").isFalse();
        var expected = new ArrayList<>(actual);
        expected.sort(comparator);
        if (descending) Collections.reverse(expected);
        assertThat(actual).as(label).isEqualTo(expected);
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
        assertThat(b).as("Product titles").isEqualTo(a);
    }
}
