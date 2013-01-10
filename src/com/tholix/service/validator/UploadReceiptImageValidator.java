/**
 * 
 */
package com.tholix.service.validator;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;

import com.tholix.domain.UploadReceiptImage;
import com.tholix.domain.UserLoginWrapper;
import com.tholix.domain.UserRegistrationWrapper;

/**
 * @author hitender 
 * @when Jan 9, 2013 8:48:41 PM
 *
 */
public class UploadReceiptImageValidator implements Validator {
	protected final Log log = LogFactory.getLog(getClass());

	@Override
	public boolean supports(Class<?> clazz) {
		return UploadReceiptImage.class.equals(clazz);
	}

	@Override
	public void validate(Object obj, Errors errors) {
		log.info("Executing validation for new userRegistration");		
		UploadReceiptImage uploadReceiptImage = (UploadReceiptImage) obj;
		if (uploadReceiptImage.getFileName().length() < 5) {
			errors.rejectValue("fileData", "field.lenght", new Object[] { Integer.valueOf("5") }, "A file name should be minimum of five characters");
		}
		
		if(!uploadReceiptImage.getFileData().getContentType().startsWith("image/")) {
			errors.rejectValue("fileData", "correct.data", new Object[] { uploadReceiptImage.getFileName() }, "Does not seems to be a right type of image file");
		}
	}

}
