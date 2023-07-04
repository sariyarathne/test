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
@Constraint(validatedBy = PromoCodeStatusValidator.class)
@Target({ElementType.METHOD, ElementType.FIELD, ElementType.PARAMETER, ElementType.ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidPromoCodeStatus {

    String message() default "Promo code update status can be either ACTIVE or CONSUMED only";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
