package com.receiptofi.web.validator;

import com.receiptofi.domain.UserProfileEntity;
import com.receiptofi.utils.Validate;
import com.receiptofi.web.form.ProfileForm;

import org.apache.commons.lang3.StringUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Value;
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

    @Value ("${AccountRegistrationController.mailLength}")
    private int mailLength;

    @Value ("${AccountRegistrationController.nameLength}")
    private int nameLength;

    @Override
    public boolean supports(Class<?> clazz) {
        return UserProfileEntity.class.equals(clazz);
    }

    @Override
    public void validate(Object obj, Errors errors) {
        LOG.debug("Executing validation");
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "firstName", "field.required", new Object[]{"First name"});
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "mail", "field.required", new Object[]{"Email address"});

        if (!errors.hasErrors()) {
            ProfileForm profileForm = (ProfileForm) obj;
            if (!Validate.isValidName(profileForm.getFirstName().getText())) {
                errors.rejectValue("firstName",
                        "field.invalid",
                        new Object[]{"First name", profileForm.getFirstName()},
                        "First name is not a valid name " + profileForm.getFirstName() + ".");
            }

            if (profileForm.getFirstName().getText().length() < nameLength) {
                errors.rejectValue("firstName",
                        "field.invalid",
                        new Object[]{"First name", profileForm.getFirstName()},
                        "First name has to be at least of size " + nameLength + " characters.");
            }

            if (StringUtils.isNotBlank(profileForm.getLastName().getText()) &&
                    !Validate.isValidName(profileForm.getLastName().getText())) {
                errors.rejectValue("lastName",
                        "field.invalid",
                        new Object[]{"Last name", profileForm.getLastName()},
                        "Last name is not a valid name " + profileForm.getLastName() + ".");
            }

            if (!Validate.isValidMail(profileForm.getMail().getText())) {
                errors.rejectValue("mail",
                        "field.email.address.not.valid",
                        new Object[]{profileForm.getMail()},
                        "Mail address is not valid mail " + profileForm.getMail() + ".");
            }

            if (profileForm.getMail() != null && profileForm.getMail().getText().length() <= mailLength) {
                errors.rejectValue("mail",
                        "field.length",
                        new Object[]{"Email address", mailLength},
                        "Mail address has to be at least of size " + mailLength + " characters.");
            }
        }
    }
}
