package com.sysco.perso.analytics.validators;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Objects;

/**
 * @author ashanthiabeyrathna
 * This class is used to validate the date time in the ISO Format with Offset
 */
public class DateTimeValidator implements ConstraintValidator<ValidDateTime, String> {

    private static final Logger logger = LoggerFactory.getLogger(DateTimeValidator.class);

    private DateTimeFormatter validDateTimeFormatter;

    @Override
    public void initialize(ValidDateTime customDate) {
        validDateTimeFormatter = DateTimeFormatter.ISO_OFFSET_DATE_TIME;
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        try {
            if (Objects.isNull(value)) {
                logger.info("Skipping date time validator since value is empty");
            } else {
                LocalDateTime.parse(value, validDateTimeFormatter);
            }
        } catch (DateTimeParseException e) {
            return false;
        }
        return true;
    }
}
