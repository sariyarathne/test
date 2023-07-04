package com.sysco.perso.analytics.validators;

import org.apache.commons.lang3.StringUtils;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

/**
 * @author ashanthiabeyrathna
 * This class is used to validate the OpcoId format
 */
public class OpcoIdFormatValidator implements ConstraintValidator<ValidOpcoIdFormat, String> {

    private String opcoIdFormatRegex;

    @Override
    public void initialize(ValidOpcoIdFormat constraintAnnotation) {
        opcoIdFormatRegex = "[0-9]+";
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        return (!StringUtils.isBlank(value) && value.length() == 3 && value.matches(opcoIdFormatRegex));
    }
}
