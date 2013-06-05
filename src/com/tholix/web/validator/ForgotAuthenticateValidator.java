package com.tholix.web.validator;

import org.apache.log4j.Logger;

import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;

import com.tholix.web.form.ForgotAuthenticateForm;

/**
 * User: hitender
 * Date: 6/4/13
 * Time: 3:11 AM
 */
public class ForgotAuthenticateValidator implements Validator {
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

        ForgotAuthenticateForm aaf = (ForgotAuthenticateForm) obj;
        if(!aaf.isEqual()) {
            errors.rejectValue("password", "field.unmatched", new Object[] { aaf.getPassword() }, "Entered value does not match");
        }
    }
}
