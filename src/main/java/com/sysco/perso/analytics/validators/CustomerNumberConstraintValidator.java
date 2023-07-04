package com.sysco.perso.analytics.validators;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.regex.Pattern;

public class CustomerNumberConstraintValidator implements ConstraintValidator<ValidCustomerNumberFormat, String> {

    private Pattern pattern;

    @Override
    public void initialize(ValidCustomerNumberFormat constraintAnnotation) {
        this.pattern = Pattern.compile(constraintAnnotation.pattern());
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null) {
            return true;
        }
        return (value.length() >= 6) && pattern.matcher(value).matches();
    }
}
