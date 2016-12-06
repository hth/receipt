package com.receiptofi.web.validator.admin;

import com.receiptofi.web.form.admin.BizForm;

import org.apache.commons.lang3.StringUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;

/**
 * User: hitender
 * Date: 8/8/13
 * Time: 11:07 AM
 */
@SuppressWarnings ({
        "PMD.BeanMembersShouldSerialize",
        "PMD.LocalVariableCouldBeFinal",
        "PMD.MethodArgumentCouldBeFinal",
        "PMD.LongVariable"
})
@Component
public class BizSearchValidator implements Validator {
    private static final Logger LOG = LoggerFactory.getLogger(BizSearchValidator.class);

    @Override
    public boolean supports(Class<?> clazz) {
        return BizForm.class.equals(clazz);
    }

    @Override
    public void validate(Object obj, Errors errors) {
        LOG.debug("Executing validation");

        BizForm bizForm = (BizForm) obj;
        if (StringUtils.isBlank(bizForm.getBusinessName()) &&
                StringUtils.isBlank(bizForm.getAddress()) &&
                StringUtils.isBlank(bizForm.getPhone())) {

            ValidationUtils.rejectIfEmptyOrWhitespace(errors, "businessName", "field.required", new Object[]{"Biz Name"});
            ValidationUtils.rejectIfEmptyOrWhitespace(errors, "address", "field.required", new Object[]{"Address"});
            ValidationUtils.rejectIfEmptyOrWhitespace(errors, "phone", "field.required", new Object[]{"Phone"});
        }
    }
}
