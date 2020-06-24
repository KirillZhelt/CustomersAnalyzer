package dev.kirillzhelt.customers.validator;

import dev.kirillzhelt.customers.validator.constraint.BeforeTodayConstraint;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.time.LocalDate;
import java.util.Objects;

public class BeforeTodayValidator implements ConstraintValidator<BeforeTodayConstraint, LocalDate> {

    @Override
    public boolean isValid(LocalDate localDate, ConstraintValidatorContext constraintValidatorContext) {
        if (localDate != null) {
            return localDate.isBefore(LocalDate.now());
        }

        return true;
    }

}
