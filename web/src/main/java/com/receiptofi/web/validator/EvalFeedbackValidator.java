package com.receiptofi.web.validator;

import com.receiptofi.web.form.EvalFeedbackForm;

import org.apache.commons.lang3.StringUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

/**
 * User: hitender
 * Date: 7/20/13
 * Time: 8:29 PM
 */
@SuppressWarnings ({
        "PMD.BeanMembersShouldSerialize",
        "PMD.LocalVariableCouldBeFinal",
        "PMD.MethodArgumentCouldBeFinal",
        "PMD.LongVariable"
})
@Component
public class EvalFeedbackValidator implements Validator {
    private static final Logger LOG = LoggerFactory.getLogger(EvalFeedbackValidator.class);

    @Override
    public boolean supports(Class<?> clazz) {
        return EvalFeedbackForm.class.equals(clazz);
    }

    @Override
    public void validate(Object obj, Errors errors) {
        LOG.debug("Executing validation");

        EvalFeedbackForm evalFeedbackForm = (EvalFeedbackForm) obj;
        if (evalFeedbackForm.getComment().getText().length() < 15) {
            errors.rejectValue("comment",
                    "field.length",
                    new Object[]{"Comment", Integer.valueOf("15")},
                    "Minimum length of 15 characters");
        }

        if (evalFeedbackForm.getFileData().getSize() != 0) {
            if (evalFeedbackForm.getFileData().getSize() > 10485760) {
                errors.rejectValue("fileData",
                        "file.length.high",
                        new Object[]{""},
                        "Uploaded file size exceeds the file size limitation of 10MB");
            }

            if (evalFeedbackForm.getFileName().length() < 5) {
                errors.rejectValue("fileData",
                        "field.length",
                        new Object[]{"Attached file", Integer.valueOf("5")},
                        "A file name should be minimum of five characters");
            }

            //Can upload SVG as image/svg+xml
            if (!evalFeedbackForm.getFileData().getContentType().startsWith("image/") && !StringUtils.isEmpty(evalFeedbackForm.getFileName())) {
                errors.rejectValue("fileData",
                        "file.data",
                        new Object[]{evalFeedbackForm.getFileName()},
                        ", is not supported. Supported format .JPEG, .JPG, .PNG");
            }
        }
    }
}
