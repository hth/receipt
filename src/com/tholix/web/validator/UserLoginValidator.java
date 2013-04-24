/**
 *
 */
package com.tholix.web.validator;

import org.apache.log4j.Logger;

import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;

import com.tholix.web.form.UserLoginForm;

/**
 * @author hitender
 * @when Dec 16, 2012 6:52:46 PM
 */
public class UserLoginValidator implements Validator {
	private static final Logger log = Logger.getLogger(UserLoginValidator.class);

	@Override
	public boolean supports(Class<?> clazz) {
		return UserLoginForm.class.equals(clazz);
	}

	@Override
	public void validate(Object obj, Errors errors) {
		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "emailId", "field.required", new Object[] { "Email ID" });
		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "password", "field.required", new Object[] { "Password" });

		UserLoginForm userLoginForm = (UserLoginForm) obj;
		if (!userLoginForm.getEmailId().matches(UserRegistrationValidator.EMAIL_REGEX)) {
			errors.rejectValue("emailId", "email.notValid", new Object[] { userLoginForm.getEmailId() }, "Not a valid email");
		}

		if (userLoginForm.getPassword().length() < 4) {
			errors.rejectValue("password", "field.length", new Object[] { Integer.valueOf("4") }, "Minimum length of four characters");
		}
	}
}
