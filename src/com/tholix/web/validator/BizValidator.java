package com.tholix.web.validator;

import org.apache.log4j.Logger;

import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;

import com.tholix.web.form.BizForm;

/**
 * User: hitender
 * Date: 5/10/13
 * Time: 12:35 AM
 */
public final class BizValidator implements Validator {
    private static final Logger log = Logger.getLogger(BizValidator.class);

    @Override
    public boolean supports(Class<?> clazz) {
        return BizForm.class.equals(clazz);
    }

    @Override
    public void validate(Object obj, Errors errors) {
        log.info("Executing validation for new bizForm");

        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "bizName.name",       "field.required", new Object[] { "Biz Name" });
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "bizStore.address",   "field.required", new Object[] { "Address" });
    }
}
