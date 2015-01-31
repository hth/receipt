package com.receiptofi.web.validator;

import com.receiptofi.domain.UserProfileEntity;
import com.receiptofi.web.controller.open.AccountRegistrationController;
import com.receiptofi.web.form.ProfileForm;
import com.receiptofi.web.form.UserProfilePreferenceForm;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;

/**
 * User: hitender
 * Date: 1/30/15 12:00 AM
 */
@SuppressWarnings ({
        "PMD.BeanMembersShouldSerialize",
        "PMD.LocalVariableCouldBeFinal",
        "PMD.MethodArgumentCouldBeFinal",
        "PMD.LongVariable"
})
@Component
public class UserProfilePreferenceValidator implements Validator {
    private static final Logger LOG = LoggerFactory.getLogger(UserProfilePreferenceValidator.class);

    @Autowired AccountRegistrationController accountRegistrationController;

    @Override
    public boolean supports(Class<?> clazz) {
        return UserProfileEntity.class.equals(clazz);
    }

    @Override
    public void validate(Object obj, Errors errors) {
        LOG.debug("Executing validation");
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "firstName", "field.required", new Object[]{"First name"});
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "mail", "field.required", new Object[]{"Email address"});

        ProfileForm profileForm = (ProfileForm) obj;
        if (profileForm.getFirstName() != null && profileForm.getFirstName().length() < accountRegistrationController.getNameLength()) {
            LOG.error("Profile first name '{}' less than size={} ", profileForm.getFirstName(), accountRegistrationController.getNameLength());
            errors.rejectValue(
                    "firstName",
                    "firstName",
                    new Object[]{accountRegistrationController.getNameLength()},
                    "First name has to be at least of size " + accountRegistrationController.getNameLength() + " characters");
        }

        if (profileForm.getMail() != null && profileForm.getMail().length() <= accountRegistrationController.getMailLength()) {
            LOG.error("Profile mail '{}' less than size={} ", profileForm.getMail(), accountRegistrationController.getMailLength());
            errors.rejectValue(
                    "lastName",
                    "lastName",
                    new Object[]{accountRegistrationController.getMailLength()},
                    "Mail Address has to be at least of size " + accountRegistrationController.getMailLength() + " characters");
        }
    }
}
