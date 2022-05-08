package pl.training.jpa;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = AccountNumberValidator.class)
public @interface AccountNumber {

    String message() default "Invalid account number";

    Class<?>[] groups() default { };

    Class<? extends Payload>[] payload() default { };

    int length() default 26;

}
