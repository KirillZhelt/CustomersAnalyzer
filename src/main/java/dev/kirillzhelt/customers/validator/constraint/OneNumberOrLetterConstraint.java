package dev.kirillzhelt.customers.validator.constraint;

import dev.kirillzhelt.customers.validator.OneNumberOrLetterValidator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.METHOD, ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = OneNumberOrLetterValidator.class)
public @interface OneNumberOrLetterConstraint {
    String message() default "Should contain at least one number or letter";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
