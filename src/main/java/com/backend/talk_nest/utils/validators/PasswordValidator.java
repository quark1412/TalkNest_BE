package com.backend.talk_nest.utils.validators;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class PasswordValidator implements ConstraintValidator<PasswordConstraint, String> {
    @Override
    public boolean isValid(String s, ConstraintValidatorContext constraintValidatorContext) {
        if (s == null) return false;

        // Contains at least 8 characters, at least 1 number and 1 special character
        String regex = "^(?=.*[0-9])(?=.*[!@#$%^&*])[A-Za-z\\d!@#$%^&*]{8,}$";
        return s.matches(regex);
    }
}
