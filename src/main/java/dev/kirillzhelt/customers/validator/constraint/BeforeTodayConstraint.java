package dev.kirillzhelt.customers.validator.constraint;


import dev.kirillzhelt.customers.validator.BeforeTodayValidator;
import dev.kirillzhelt.customers.validator.OneNumberOrLetterValidator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.METHOD, ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = BeforeTodayValidator.class)
public @interface BeforeTodayConstraint {
    String message() default "Should be before current date";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
