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
    private final String textWithExpressions;
    public Template(String textWithExpressions) {
        this.textWithExpressions = textWithExpressions;
    }

    public String evaluate(Map<String, String> values) {
        validate(values);
        return substitute(values);
    }

    private void validate(Map<String, String> values) {
        if (isValuesComplete(values)) {
            throw new IllegalArgumentException();
        }
    }

    private String substitute(Map<String, String> values) {
        var result = textWithExpressions;
        for (Entry<String, String> entry : values.entrySet()) {
            result = result.replaceAll(createExpression(entry), entry.getValue());
        }
        return result;
    }

    private String createExpression(Entry<String, String> entry) {
        return EXPRESSION_START + entry.getKey() + EXPRESSION_END;
    }

    private boolean isValuesComplete(Map<String, String> values) {
        return values.size() != getExpressions().count();
    }

    private Stream<MatchResult> getExpressions() {
        return EXPRESSION.matcher(textWithExpressions).results();
    }

}
