package com.receiptofi.web.validator;

import com.receiptofi.domain.UserProfileEntity;
import com.receiptofi.utils.Validate;
import com.receiptofi.web.controller.open.AccountRegistrationController;
import com.receiptofi.web.form.ProfileForm;

import org.apache.commons.lang3.StringUtils;

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
public class ProfileValidator implements Validator {
    private static final Logger LOG = LoggerFactory.getLogger(ProfileValidator.class);

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
                    "First name has to be at least of size " + accountRegistrationController.getNameLength() + " characters.");
        }

        if (profileForm.getMail() != null && profileForm.getMail().length() <= accountRegistrationController.getMailLength()) {
            LOG.error("Profile mail '{}' less than size={} ", profileForm.getMail(), accountRegistrationController.getMailLength());
            errors.rejectValue(
                    "mail",
                    "mail",
                    new Object[]{accountRegistrationController.getMailLength()},
                    "Mail Address has to be at least of size " + accountRegistrationController.getMailLength() + " characters.");
        }

        if (!Validate.isValidName(profileForm.getFirstName())) {
            LOG.error("Profile first name '{}' is not a valid name", profileForm.getFirstName());
            errors.rejectValue(
                    "firstName",
                    "firstName",
                    new Object[]{profileForm.getFirstName()},
                    "First Name is not a valid name " + profileForm.getFirstName() + ".");
        }

        if (StringUtils.isNotBlank(profileForm.getLastName()) && !Validate.isValidName(profileForm.getLastName())) {
            LOG.error("Profile last name '{}' is not a name", profileForm.getLastName());
            errors.rejectValue(
                    "lastName",
                    "lastName",
                    new Object[]{profileForm.getLastName()},
                    "Last Name is not a valid name " + profileForm.getLastName() + ".");
        }

        if (!Validate.isValidMail(profileForm.getMail())) {
            LOG.error("Profile mail '{}' is not a valid mail", profileForm.getMail());
            errors.rejectValue(
                    "mail",
                    "mail",
                    new Object[]{profileForm.getMail()},
                    "Mail Address is not valid mail " + profileForm.getMail() + ".");
        }
    }
}
