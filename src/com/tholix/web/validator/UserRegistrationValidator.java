/**
 *
 */
package com.tholix.web.validator;

import org.apache.log4j.Logger;

import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;

import com.tholix.web.form.UserRegistrationForm;

/**
 * @author hitender
 * @since Dec 25, 2012 12:17:57 PM
 *
 */
public class UserRegistrationValidator implements Validator {
	private static final Logger log = Logger.getLogger(UserRegistrationValidator.class);

	public static final String EMAIL_REGEX = "^[_a-z0-9-]+(\\.[_a-z0-9-]+)*@[a-z0-9-]+(\\.[a-z0-9-]+)*(\\.[a-z]{2,4})$";

	@Override
	public boolean supports(Class<?> clazz) {
		return UserRegistrationForm.class.equals(clazz);
	}

	@Override
	public void validate(Object obj, Errors errors) {
		log.debug("Executing validation for new userRegistration");
		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "firstName", "field.required", new Object[] { "First Name" });
		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "lastName", "field.required", new Object[] { "Last Name" });
		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "emailId", "field.required", new Object[] { "Email ID" });
		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "password", "field.required", new Object[] { "Password" });

		UserRegistrationForm userRegistration = (UserRegistrationForm) obj;
		if (userRegistration.getFirstName() != null && userRegistration.getFirstName().length() < 4) {
			errors.rejectValue("firstName",
                    "field.length",
                    new Object[] { Integer.valueOf("4") },
                    "Minimum length of four characters");
		}

		if (userRegistration.getLastName() != null && userRegistration.getLastName().length() < 4) {
			errors.rejectValue("lastName",
                    "field.length",
                    new Object[] { Integer.valueOf("4") },
                    "Minimum length of four characters");
		}

		if (userRegistration.getEmailId() != null && !userRegistration.getEmailId().matches(EMAIL_REGEX)) {
			errors.rejectValue("emailId",
                    "email.notValid",
                    new Object[] { userRegistration.getEmailId() },
                    "Not a valid Email Address");
		}

		if (userRegistration.getPassword() != null && userRegistration.getPassword().length() < 4) {
			errors.rejectValue("password",
                    "field.length",
                    new Object[] { Integer.valueOf("4") },
                    "Minimum length of four characters");
		}
	}

    public void accountExists(Object obj, Errors errors) {
        UserRegistrationForm userRegistration = (UserRegistrationForm) obj;
        errors.rejectValue("emailId",
                "emailId.already.registered",
                new Object[] { userRegistration.getEmailId() },
                "Account already registered with this Email ID");
    }
}
