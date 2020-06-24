package dev.kirillzhelt.customers.validator;

import dev.kirillzhelt.customers.validator.constraint.OneNumberOrLetterConstraint;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class OneNumberOrLetterValidator implements ConstraintValidator<OneNumberOrLetterConstraint, String> {

    @Override
    public boolean isValid(String s, ConstraintValidatorContext constraintValidatorContext) {
        if (s != null) {
            return s.matches(".*([a-zA-Z]+|[0-9]+|[А-ЯЁ]+|[-А-яЁё]+).*");
        }

        return true;
    }
}
