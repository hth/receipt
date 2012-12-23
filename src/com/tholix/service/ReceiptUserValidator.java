/**
 * 
 */
package com.tholix.service;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import com.tholix.domain.ReceiptUser;

/**
 * @author hitender 
 * @when Dec 16, 2012 6:52:46 PM
 */
public class ReceiptUserValidator implements Validator {

	protected final Log logger = LogFactory.getLog(getClass());

	@Override
	public boolean supports(Class<?> clazz) {
		return ReceiptUser.class.equals(clazz);
	}

	@Override
	public void validate(Object obj, Errors errors) {
		ReceiptUser receiptUser = (ReceiptUser) obj;
		if (receiptUser == null) {
			errors.rejectValue("receiptUser", "error.not-specified", null, "Value required.");
		} else {
			logger.info("Validating with " + receiptUser + ": '" + receiptUser.getEmailId() + "'");
			if (receiptUser.getEmailId() == null || receiptUser.getEmailId().length() == 0) {
				logger.info("Validating error " + receiptUser + ": " + receiptUser.getEmailId());
				errors.rejectValue("receiptUser", "error.too-high", new Object[] { receiptUser.getEmailId() }, "Cannot have empty Email Id.");
			}
		}

	}

}
