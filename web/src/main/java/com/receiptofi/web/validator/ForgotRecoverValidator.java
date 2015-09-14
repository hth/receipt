package com.receiptofi.web.validator;

import com.receiptofi.web.form.ForgotRecoverForm;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.validator.routines.EmailValidator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;

/**
 * User: hitender
 * Date: 5/31/13
 * Time: 8:29 PM
 */
@SuppressWarnings ({
        "PMD.BeanMembersShouldSerialize",
        "PMD.LocalVariableCouldBeFinal",
        "PMD.MethodArgumentCouldBeFinal",
        "PMD.LongVariable"
})
@Component
public class ForgotRecoverValidator implements Validator {
    private static final Logger LOG = LoggerFactory.getLogger(ForgotRecoverValidator.class);

    @Override
    public boolean supports(Class<?> clazz) {
        return ForgotRecoverForm.class.equals(clazz);
    }

    @Override
    public void validate(Object obj, Errors errors) {
        LOG.debug("Executing validation");

        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "mail", "field.required", new Object[]{"Email address"});

        ForgotRecoverForm frf = (ForgotRecoverForm) obj;
        if (StringUtils.isNotEmpty(frf.getCaptcha())) {
            errors.rejectValue("captcha", "field.unmatched", new Object[]{""}, "Entered value does not match");
        }

        if (!errors.hasErrors()) {
            EmailValidator emailValidator = EmailValidator.getInstance();
            if (!emailValidator.isValid(frf.getMail().getText().toLowerCase())) {
                errors.rejectValue(
                        "mail",
                        "field.email.address.not.valid",
                        new Object[]{frf.getMail().getText()},
                        "Email address provided is not valid");
            }
        }
    }
}
