package pl.training.jpa.extras;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static java.util.Collections.emptyMap;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class TemplateTest {

    private static final String TEXT_WITHOUT_EXPRESSIONS = "My name is Jan Kowalski";
    private static final String TEXT_WITH_EXPRESSIONS = "My name is ${firstName} ${lastName}";

    @Test
    void given_a_text_without_expressions_when_evaluate_then_returns_the_text() {
        var template = new Template(TEXT_WITHOUT_EXPRESSIONS);
        assertEquals(TEXT_WITHOUT_EXPRESSIONS, template.evaluate(emptyMap()));
    }


    @DisplayName("given a text with expressions")
    @Nested
    class GivenTextWithExpressions {

        @Test
        void when_evaluate_then_returns_the_text_with_substituted_value() {
            var values = Map.of("firstName", "Jan", "lastName", "Kowalski");
            var template = new Template(TEXT_WITH_EXPRESSIONS);
            assertEquals(TEXT_WITHOUT_EXPRESSIONS, template.evaluate(values));
        }

        @Test
        void when_evaluating_without_providing_all_values_then_throws_exception() {
            assertThrows(IllegalArgumentException.class, () -> new Template(TEXT_WITH_EXPRESSIONS).evaluate(emptyMap()));
        }

        @Test
        void when_evaluating_evaluating_with_non_alphanumeric_values_then_throws_exception() {
            var values = Map.of("firstName", "@", "lastName", "#");
            var template = new Template(TEXT_WITH_EXPRESSIONS);
            assertThrows(IllegalArgumentException.class, () -> template.evaluate(values));
        }

    }

}
