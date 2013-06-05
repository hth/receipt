package com.tholix.web.validator;

import org.apache.log4j.Logger;

import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;

import com.tholix.web.form.ForgotRecoverForm;

/**
 * User: hitender
 * Date: 5/31/13
 * Time: 8:29 PM
 */
public class ForgotRecoverValidator implements Validator {
    private static final Logger log = Logger.getLogger(ForgotRecoverValidator.class);

    @Override
    public boolean supports(Class<?> clazz) {
        return ForgotRecoverForm.class.equals(clazz);
    }

    @Override
    public void validate(Object obj, Errors errors) {
        log.info("Executing validation for new bizForm");

        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "emailId", "field.required", new Object[]{"EmailId"});
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "captchaConfirm", "field.required", new Object[]{"Captcha"});

        ForgotRecoverForm arf = (ForgotRecoverForm) obj;
        if(!arf.isCaptchaEqual()) {
            errors.rejectValue("captchaConfirm", "field.unmatched", new Object[] { arf.getCaptchaConfirm() }, "Entered value does not match");
        }
    }
}
