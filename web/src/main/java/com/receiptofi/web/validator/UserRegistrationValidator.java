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

    @Value ("${AccountRegistrationController.mailLength}")
    private int mailLength;

    @Value ("${AccountRegistrationController.nameLength}")
    private int nameLength;

    @Value ("${AccountRegistrationController.passwordLength}")
    private int passwordLength;

    @Override
    public boolean supports(Class<?> clazz) {
        return UserRegistrationForm.class.equals(clazz);
    }

    @Override
    public void validate(Object obj, Errors errors) {
        LOG.debug("Executing validation");
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "firstName", "field.required", new Object[]{"First Name"});

        /** Example of validation message: Email Address cannot be left blank. */
        /** Example of validation message: Email Address field.required. */
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "mail", "field.required", new Object[]{"Email Address"});
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "password", "field.required", new Object[]{"Password"});

        if (!errors.hasErrors()) {
            UserRegistrationForm userRegistration = (UserRegistrationForm) obj;
            if (userRegistration.getFirstName().length() < nameLength) {
                errors.rejectValue("firstName",
                        "field.length",
                        new Object[]{nameLength},
                        "Minimum length of four characters");
            }

            if (!Validate.isValidMail(userRegistration.getMail())) {
                errors.rejectValue("mail",
                        "field.email.address.not.valid",
                        new Object[]{userRegistration.getMail()},
                        "Email Address provided is not valid");
            }

            if (userRegistration.getMail() != null && userRegistration.getMail().length() <= mailLength) {
                errors.rejectValue(
                        "mail",
                        "field.length",
                        new Object[]{mailLength},
                        "Email Address has to be at least of size " + mailLength + " characters.");
            }

            if (userRegistration.getPassword().length() < passwordLength) {
                errors.rejectValue("password",
                        "field.length",
                        new Object[]{passwordLength},
                        "Minimum length of " + passwordLength + " characters");
            }
        }
    }

    public void accountExists(Object obj, Errors errors) {
        UserRegistrationForm userRegistration = (UserRegistrationForm) obj;
        errors.rejectValue("mail",
                "emailId.already.registered",
                new Object[]{userRegistration.getMail()},
                "Account already registered with this Email Address");
    }
}
