package com.sysco.perso.analytics.validators;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author ashanthiabeyrathna
 */
@Documented
@Constraint(validatedBy = ModifiedSourceValidator.class)
@Target({ElementType.METHOD, ElementType.FIELD, ElementType.ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidModifiedSource {

    String message() default "Modified source can be either SUS, SHOP_WEB or SHOP_MOBILE only";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

    String[] acceptedValues() default {};
}
