package com.receiptofi.web.validator.admin;

import com.receiptofi.web.form.admin.BizForm;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;

/**
 * User: hitender
 * Date: 5/10/13
 * Time: 12:35 AM
 */
@SuppressWarnings ({
        "PMD.BeanMembersShouldSerialize",
        "PMD.LocalVariableCouldBeFinal",
        "PMD.MethodArgumentCouldBeFinal",
        "PMD.LongVariable"
})
@Component
public class BizValidator implements Validator {
    private static final Logger LOG = LoggerFactory.getLogger(BizValidator.class);

    @Override
    public boolean supports(Class<?> clazz) {
        return BizForm.class.equals(clazz);
    }

    @Override
    public void validate(Object obj, Errors errors) {
        LOG.debug("Executing validation");

        ValidationUtils.rejectIfEmptyOrWhitespace(
                errors,
                "businessName",
                "field.required",
                new Object[]{"Biz Name"}
        );
        ValidationUtils.rejectIfEmptyOrWhitespace(
                errors,
                "address",
                "field.required",
                new Object[]{"Address"}
        );
    }
}
