package pl.training.jpa.extras;

import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.MatchResult;
import java.util.regex.Pattern;
import java.util.stream.Stream;

public class Template {

    private static final String EXPRESSION_START = "\\$\\{";
    private static final String EXPRESSION_END = "}";
    private static final Pattern EXPRESSION = Pattern.compile(EXPRESSION_START + "\\w+" + EXPRESSION_END);

    private static final String INVALID_VALUE = ".*\\W+.*";
    private final String textWithExpressions;
    public Template(String textWithExpressions) {
        this.textWithExpressions = textWithExpressions;
    }

    public String evaluate(Map<String, String> values) {
        validate(values);
        return substitute(values);
    }

    private void validate(Map<String, String> values) {
        if (!isComplete(values) || !isAlphanumeric(values)) {
            throw new IllegalArgumentException();
        }
    }

    private String substitute(Map<String, String> values) {
        var result = textWithExpressions;
        for (Entry<String, String> entry : values.entrySet()) {
            var expression = createExpression(entry.getKey());
            result = result.replaceAll(expression, entry.getValue());
        }
        return result;
    }

    private String createExpression(String key) {
        return EXPRESSION_START + key + EXPRESSION_END;
    }

    private boolean isComplete(Map<String, String> values) {
        return values.size() == getExpressions().count();
    }

    private boolean isAlphanumeric(Map<String, String> values) {
        return values.values().stream().noneMatch(value -> value.matches(INVALID_VALUE));
    }

    private Stream<MatchResult> getExpressions() {
        return EXPRESSION.matcher(textWithExpressions).results();
    }

}
