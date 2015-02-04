/**
 *
 */
package com.receiptofi.web.validator;

import com.receiptofi.utils.Validate;
import com.receiptofi.web.controller.open.AccountRegistrationController;
import com.receiptofi.web.form.UserRegistrationForm;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;

/**
 * @author hitender
 * @since Dec 25, 2012 12:17:57 PM
 */
@SuppressWarnings ({
        "PMD.BeanMembersShouldSerialize",
        "PMD.LocalVariableCouldBeFinal",
        "PMD.MethodArgumentCouldBeFinal",
        "PMD.LongVariable"
})
@Component
public class UserRegistrationValidator implements Validator {
    private static final Logger LOG = LoggerFactory.getLogger(UserRegistrationValidator.class);

    @Autowired AccountRegistrationController accountRegistrationController;

    @Override
    public boolean supports(Class<?> clazz) {
        return UserRegistrationForm.class.equals(clazz);
    }

    @Override
    public void validate(Object obj, Errors errors) {
        LOG.debug("Executing validation");
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "firstName", "field.required", new Object[]{"First Name"});
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "emailId", "field.required", new Object[]{"Email ID"});
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "password", "field.required", new Object[]{"Password"});

        UserRegistrationForm userRegistration = (UserRegistrationForm) obj;
        if (userRegistration.getFirstName().length() < accountRegistrationController.getNameLength()) {
            errors.rejectValue("firstName",
                    "field.length",
                    new Object[]{accountRegistrationController.getNameLength()},
                    "Minimum length of four characters");
        }

        if (!Validate.isValidMail(userRegistration.getEmailId())) {
            errors.rejectValue("emailId",
                    "field.email.address.not.valid",
                    new Object[]{userRegistration.getEmailId()},
                    "Email Address provided is not valid");
        }

        if (userRegistration.getPassword().length() < accountRegistrationController.getPasswordLength()) {
            errors.rejectValue("password",
                    "field.length",
                    new Object[]{accountRegistrationController.getPasswordLength()},
                    "Minimum length of four characters");
        }
    }

    public void accountExists(Object obj, Errors errors) {
        UserRegistrationForm userRegistration = (UserRegistrationForm) obj;
        errors.rejectValue("emailId",
                "emailId.already.registered",
                new Object[]{userRegistration.getEmailId()},
                "Account already registered with this Email");
    }
}
