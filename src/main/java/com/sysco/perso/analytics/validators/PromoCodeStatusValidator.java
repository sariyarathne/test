package com.sysco.perso.analytics.validators;

import com.sysco.perso.analytics.entity.enums.PromoCodeStatus;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.Arrays;
import java.util.List;

/**
 * @author ashanthiabeyrathna
 * This class is used to validate the Promo Code status
 */
public class PromoCodeStatusValidator implements ConstraintValidator<ValidPromoCodeStatus, String> {

    private List<String> valueList;

    @Override
    public void initialize(ValidPromoCodeStatus constraintAnnotation) {
        valueList = Arrays.asList(PromoCodeStatus.getNames(PromoCodeStatus.class));
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        return valueList.contains(value);
    }
}
