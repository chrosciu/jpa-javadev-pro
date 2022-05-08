package pl.training.jpa.extras;

import org.hamcrest.Description;
import org.hamcrest.TypeSafeMatcher;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.List;

import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.text.CharSequenceLength.hasLength;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static pl.training.jpa.extras.Digits.isDigits;

class ExampleTest {

    @Test
    void matcher_test() {
        assertThat(List.of(1, 2, 3, 4, 5), hasItems(2, 4));
        assertThat("Java is old", both(containsString("va")).and(hasLength(11)));
        assertThat("2.22", isDigits());
    }

    @ValueSource(ints = {1, 2})
    @ParameterizedTest(name = "Invalid value {0}")
    void sum_test(int a) {
        assertTrue(a >= 0);
    }

}

class Digits extends TypeSafeMatcher<String> {

    public static Digits isDigits() {
        return new Digits();
    }

    @Override
    protected boolean matchesSafely(String value) {
        boolean result;
        try {
            Double.parseDouble(value);
            result = true;
        } catch (NumberFormatException e) {
            result = false;
        }
        return result;
    }

    @Override
    public void describeTo(Description description) {
        description.appendText("should contains digits");
    }

}
