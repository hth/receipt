package com.tholix.web.validator;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;

import com.tholix.web.form.BizForm;

/**
 * User: hitender
 * Date: 8/8/13
 * Time: 11:07 AM
 */
public final class BizSearchValidator implements Validator {
    private static final Logger log = Logger.getLogger(BizSearchValidator.class);

    @Override
    public boolean supports(Class<?> clazz) {
        return BizForm.class.equals(clazz);
    }

    @Override
    public void validate(Object obj, Errors errors) {
        log.info("Executing validation for new bizForm");

        BizForm bizForm = (BizForm) obj;
        if(StringUtils.isEmpty(bizForm.getBizName().getName()) && StringUtils.isEmpty(bizForm.getBizStore().getAddress()) && StringUtils.isEmpty(bizForm.getBizStore().getPhone())) {
            ValidationUtils.rejectIfEmptyOrWhitespace(errors, "bizName.name", "field.required", new Object[]{"Biz Name"});
            ValidationUtils.rejectIfEmptyOrWhitespace(errors, "bizStore.address",   "field.required", new Object[] { "Address" });
            ValidationUtils.rejectIfEmptyOrWhitespace(errors, "bizStore.phone",   "field.required", new Object[] { "Phone" });
        }
    }
}
