/**
 * 
 */
package com.tholix.service.validator;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;

import com.tholix.domain.UserLoginWrapper;

/**
 * @author hitender
 * @when Dec 16, 2012 6:52:46 PM
 */
public class UserValidator implements Validator {

	protected final Log logger = LogFactory.getLog(getClass());

	@Override
	public boolean supports(Class<?> clazz) {
		return UserLoginWrapper.class.equals(clazz);
	}

	@Override
	public void validate(Object obj, Errors errors) {
		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "emailId", "field.required", new Object[] { "Email ID" });
		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "password", "field.required", new Object[] { "Password" });

		UserLoginWrapper userLoginWrapper = (UserLoginWrapper) obj;
		if (!userLoginWrapper.getEmailId().matches(NewUserValidator.EMAIL_REGEX)) {
			errors.rejectValue("emailId", "email.notValid", new Object[] { userLoginWrapper.getEmailId() }, "Not a valid email");
		}

		if (userLoginWrapper.getPassword().length() < 4) {
			errors.rejectValue("password", "field.lenght", new Object[] { Integer.valueOf("4") }, "Minimum length of four characters");
		}
	}
}
