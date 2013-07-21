package com.tholix.web.validator;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import com.tholix.web.form.FeedbackForm;


/**
 * User: hitender
 * Date: 7/20/13
 * Time: 8:29 PM
 */
public final class FeedbackValidator implements  Validator {
    private static final Logger log = Logger.getLogger(FeedbackValidator.class);

    @Override
    public boolean supports(Class<?> clazz) {
        return FeedbackForm.class.equals(clazz);
    }

    @Override
    public void validate(Object obj, Errors errors) {
        log.info("Executing validation for new uploadReceiptImageValidator");

        FeedbackForm feedbackForm = (FeedbackForm) obj;
        if(feedbackForm.getComment().length() < 15) {
            errors.rejectValue("comment",
                    "field.length",
                    new Object[] { Integer.valueOf("15") },
                    "Minimum length of 15 characters");
        }

        if(feedbackForm.getFileData().getSize() == 0) {
            errors.rejectValue("fileData", "file.length.empty", new Object[] { "" }, "There seems to be no file or a file of empty size found");
        }

        if(feedbackForm.getFileData().getSize() > 10485760) {
            errors.rejectValue("fileData", "file.length.high", new Object[] { "" }, "Uploaded file size exceeds the file size limitation of 10MB");
        }

        if (feedbackForm.getFileName().length() < 5) {
            errors.rejectValue("fileData", "field.length", new Object[] { Integer.valueOf("5") }, "A file name should be minimum of five characters");
        }

        //Can upload SVG as image/svg+xml
        if(!feedbackForm.getFileData().getContentType().startsWith("image/") && !StringUtils.isEmpty(feedbackForm.getFileName())) {
            errors.rejectValue("fileData", "file.data", new Object[] { feedbackForm.getFileName() }, ", is not supported. Supported format .JPEG, .JPG, .PNG");
        }
    }
}