package pl.training.jpa;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class AccountNumberValidator implements ConstraintValidator<AccountNumber, String> {

    private int length;

    @Override
    public void initialize(AccountNumber constraintAnnotation) {
       length = constraintAnnotation.length();
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        return value.length() == length;
    }

}
