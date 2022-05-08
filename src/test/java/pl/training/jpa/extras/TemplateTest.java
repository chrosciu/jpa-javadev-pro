package pl.training.jpa.extras;

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

    @Test
    void given_a_text_with_expressions_when_evaluate_then_returns_the_text_with_substituted_value() {
        var values = Map.of("firstName", "Jan", "lastName", "Kowalski");
        var template = new Template(TEXT_WITH_EXPRESSIONS);
        assertEquals(TEXT_WITHOUT_EXPRESSIONS, template.evaluate(values));
    }

    @Test
    void given_a_text_with_expressions_when_evaluating_without_providing_all_values_then_throws_exception() {
        assertThrows(IllegalArgumentException.class, () -> new Template(TEXT_WITH_EXPRESSIONS).evaluate(emptyMap()));
    }

}
