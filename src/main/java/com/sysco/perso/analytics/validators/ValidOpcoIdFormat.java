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
@Constraint(validatedBy = OpcoIdFormatValidator.class)
@Target({ElementType.METHOD, ElementType.FIELD, ElementType.ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidOpcoIdFormat {

    String message() default "Given opco id is not in valid format, ex: 042, 450";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
