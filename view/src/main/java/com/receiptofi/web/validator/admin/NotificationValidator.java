package com.receiptofi.web.validator.admin;

import com.receiptofi.web.form.admin.BizForm;
import com.receiptofi.web.form.admin.NotificationSendForm;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;

/**
 * User: hitender
 * Date: 11/27/16 7:01 PM
 */
@SuppressWarnings ({
        "PMD.BeanMembersShouldSerialize",
        "PMD.LocalVariableCouldBeFinal",
        "PMD.MethodArgumentCouldBeFinal",
        "PMD.LongVariable"
})
@Component
public class NotificationValidator implements Validator {
    private static final Logger LOG = LoggerFactory.getLogger(NotificationValidator.class);

    @Override
    public boolean supports(Class<?> clazz) {
        return BizForm.class.equals(clazz);
    }

    @Override
    public void validate(Object obj, Errors errors) {
        LOG.debug("Executing validation");

        NotificationSendForm notificationSendForm = (NotificationSendForm) obj;

        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "rid", "field.required", new Object[]{"RID"});
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "message", "field.required", new Object[]{"Message"});
    }
}
