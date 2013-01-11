/**
 * 
 */
package com.tholix.service.validator;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;

import com.tholix.utils.DateUtil;
import com.tholix.web.form.ReceiptForm;

/**
 * @author hitender 
 * @when Jan 10, 2013 10:00:24 PM
 *
 */
public class ReceiptFormValidator implements Validator {
	protected final Log log = LogFactory.getLog(getClass());

	@Override
	public boolean supports(Class<?> clazz) {
		return ReceiptForm.class.equals(clazz);
	}

	@Override
	public void validate(Object obj, Errors errors) {
		log.info("Executing validation for new receiptForm");	
		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "receipt.title", "field.required", new Object[] { "Title" });
		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "receipt.receiptDate", "field.required", new Object[] { "Date" });
		
		ReceiptForm receiptForm = (ReceiptForm) obj;		
		try {
			DateUtil.getDateFromString(receiptForm.getReceipt().getReceiptDate());
		} catch(Exception exce) {
			errors.rejectValue("receipt.receiptDate", "field.date", new Object[] { "" }, "Unsupported date format");
		}
		
			
	}

}
