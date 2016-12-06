package com.receiptofi.web.validator;

import com.receiptofi.utils.Validate;
import com.receiptofi.web.form.InviteAuthenticateForm;

import org.apache.commons.lang3.StringUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;

/**
 * User: hitender
 * Date: 6/9/13
 * Time: 5:39 PM
 */
@SuppressWarnings ({
        "PMD.BeanMembersShouldSerialize",
        "PMD.LocalVariableCouldBeFinal",
        "PMD.MethodArgumentCouldBeFinal",
        "PMD.LongVariable"
})
@Component
public class InviteAuthenticateValidator implements Validator {
    private static final Logger LOG = LoggerFactory.getLogger(InviteAuthenticateValidator.class);

    @Value ("${AccountRegistrationController.mailLength}")
    private int mailLength;

    @Value ("${AccountRegistrationController.nameLength}")
    private int nameLength;

    @Value ("${AccountRegistrationController.passwordLength}")
    private int passwordLength;

    @Override
    public boolean supports(Class<?> clazz) {
        return InviteAuthenticateForm.class.equals(clazz);
    }

    @Override
    public void validate(Object obj, Errors errors) {
        LOG.debug("Executing validation");

        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "firstName", "field.required", new Object[]{"First name"});
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "mail", "field.required", new Object[]{"Email address"});
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "forgotAuthenticateForm.password", "field.required", new Object[]{"Password"});

        if (!errors.hasErrors()) {
            InviteAuthenticateForm faa = (InviteAuthenticateForm) obj;
            if (!Validate.isValidName(faa.getFirstName().getText())) {
                LOG.warn("Profile first name '{}' is not a valid name", faa.getFirstName());
                errors.rejectValue("firstName",
                        "field.invalid",
                        new Object[]{"First name", faa.getFirstName()},
                        "First Name is not a valid name " + faa.getFirstName());
            }

            if (faa.getFirstName() != null && faa.getFirstName().getText().length() < nameLength) {
                errors.rejectValue("firstName",
                        "field.length",
                        new Object[]{"First mame", nameLength},
                        "Minimum length of " + nameLength + " characters");
            }

            if (StringUtils.isNotBlank(faa.getLastName().getText()) && !Validate.isValidName(faa.getLastName().getText())) {
                errors.rejectValue("lastName",
                        "field.invalid",
                        new Object[]{"Last name", faa.getLastName()},
                        "Last Name is not a valid name " + faa.getLastName());
            }

            if (!Validate.isValidMail(faa.getMail().getText())) {
                errors.rejectValue("mail",
                        "field.email.address.not.valid",
                        new Object[]{faa.getMail()},
                        "Email address provided is not valid");
            }

            if (faa.getMail() != null && faa.getMail().getText().length() <= mailLength) {
                errors.rejectValue(
                        "mail",
                        "field.length",
                        new Object[]{"Email address", mailLength},
                        "Email Address has to be at least of size " + mailLength + " characters");
            }

            if (faa.getForgotAuthenticateForm().getPassword().length() < passwordLength) {
                errors.rejectValue("forgotAuthenticateForm.password",
                        "field.length",
                        new Object[]{"Password", passwordLength},
                        "Minimum length of " + passwordLength + " characters");
            }

            if (!faa.isAcceptsAgreement()) {
                if (errors.hasErrors()) {
                    errors.rejectValue("acceptsAgreement",
                            "agreement.checkbox",
                            new Object[]{""},
                            "To continue, please check accept to terms");
                } else {
                    errors.rejectValue("acceptsAgreement",
                            "agreement.checkbox",
                            new Object[]{"to continue"},
                            "To continue, please check accept to terms");
                }
            }
        }
    }
}
