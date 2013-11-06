package com.receiptofi.web.validator;

import org.apache.log4j.Logger;

import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;

import com.receiptofi.web.form.ForgotAuthenticateForm;

/**
 * User: hitender
 * Date: 6/4/13
 * Time: 3:11 AM
 */
public final class ForgotAuthenticateValidator implements Validator {
    private static final Logger log = Logger.getLogger(ForgotAuthenticateValidator.class);

    @Override
    public boolean supports(Class<?> clazz) {
        return ForgotAuthenticateForm.class.equals(clazz);
    }

    @Override
    public void validate(Object obj, Errors errors) {
        log.info("Executing validation for new bizForm");

        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "password", "field.required", new Object[]{"Password"});
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "passwordSecond", "field.required", new Object[]{"Retype Password"});

        ForgotAuthenticateForm faa = (ForgotAuthenticateForm) obj;
        if(!faa.isEqual()) {
            errors.rejectValue("password", "field.unmatched", new Object[] { "" }, "Password entered value does not match");
            errors.rejectValue("passwordSecond", "field.unmatched", new Object[] { "" }, "Password entered value does not match");
        }

        if (faa.getPassword().length() < 4) {
            errors.rejectValue("password",
                    "field.length",
                    new Object[] { Integer.valueOf("4") },
                    "Minimum length of four characters");
        }

        if (faa.getPasswordSecond().length() < 4) {
            errors.rejectValue("passwordSecond",
                    "field.length",
                    new Object[] { Integer.valueOf("4") },
                    "Minimum length of four characters");
        }
    }
}
