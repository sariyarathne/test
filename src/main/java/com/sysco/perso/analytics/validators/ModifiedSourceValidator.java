package com.sysco.perso.analytics.validators;

import com.sysco.perso.analytics.entity.enums.ModifiedSource;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.Arrays;
import java.util.List;

/**
 * @author ashanthiabeyrathna
 * This class is used to validate modified source
 */
public class ModifiedSourceValidator implements ConstraintValidator<ValidModifiedSource, String> {

    private List<String> valueList;

    @Override
    public void initialize(ValidModifiedSource constraintAnnotation) {
        valueList = Arrays.asList(ModifiedSource.getNames(ModifiedSource.class));
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        //To Do Validate modified source against the Apigee app name
        return valueList.contains(value);
    }
}
