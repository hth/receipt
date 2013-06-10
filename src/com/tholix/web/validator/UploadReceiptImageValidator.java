/**
 *
 */
package com.tholix.web.validator;

import org.apache.log4j.Logger;

import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import com.tholix.domain.UploadReceiptImage;

/**
 * @author hitender
 * @since Jan 9, 2013 8:48:41 PM
 *
 */
public class UploadReceiptImageValidator implements Validator {
	private static final Logger log = Logger.getLogger(UploadReceiptImageValidator.class);

	@Override
	public boolean supports(Class<?> clazz) {
		return UploadReceiptImage.class.equals(clazz);
	}

	@Override
	public void validate(Object obj, Errors errors) {
		log.info("Executing validation for new uploadReceiptImageValidator");

		UploadReceiptImage uploadReceiptImage = (UploadReceiptImage) obj;
		if(uploadReceiptImage.getFileData().getSize() == 0) {
			errors.rejectValue("fileData", "file.length.empty", new Object[] { "" }, "There seems to be no file or a file of empty size found");
		}

		if(uploadReceiptImage.getFileData().getSize() > 10485760) {
			errors.rejectValue("fileData", "file.length.high", new Object[] { "" }, "Uploaded file size exceeds the file size limitation of 10MB");
		}

		if (uploadReceiptImage.getFileName().length() < 5) {
			errors.rejectValue("fileData", "field.length", new Object[] { Integer.valueOf("5") }, "A file name should be minimum of five characters");
		}

		if(!uploadReceiptImage.getFileData().getContentType().startsWith("image/")) {
			errors.rejectValue("fileData", "file.data", new Object[] { uploadReceiptImage.getFileName() }, ", is not supported. Supported format .JPEG, .JPG, .PNG");
		}
	}

}
