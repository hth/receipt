package com.receiptofi.web.flow.validator;

import com.receiptofi.domain.flow.Register;
import com.receiptofi.utils.Constants;
import com.receiptofi.utils.Formatter;
import com.receiptofi.utils.Validate;
import com.receiptofi.web.controller.access.LandingController;

import org.apache.commons.lang3.StringUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.binding.message.MessageBuilder;
import org.springframework.binding.message.MessageContext;
import org.springframework.stereotype.Component;

/**
 * User: hitender
 * Date: 8/4/16 11:25 AM
 */
@Component
public class UserFlowValidator {
    private static final Logger LOG = LoggerFactory.getLogger(UserFlowValidator.class);

    @Value ("${AccountRegistrationController.mailLength}")
    private int mailLength;

    @Value ("${AccountRegistrationController.nameLength}")
    private int nameLength;

    @Value ("${AccountRegistrationController.passwordLength}")
    private int passwordLength;

    /**
     * Validate business user profile.
     *
     * @param register
     * @param messageContext
     * @return
     */
    @SuppressWarnings ("unused")
    public String validateUserProfileSignupDetails(Register register, MessageContext messageContext) {
        LOG.info("Validate user profile signup rid={}", register.getRegisterUser().getRid());
        String status = validateUserProfileDetails(register, messageContext);

        if (StringUtils.isNotBlank(register.getRegisterUser().getBirthday()) && !Constants.AGE_RANGE.matcher(register.getRegisterUser().getBirthday()).matches()) {
            messageContext.addMessage(
                    new MessageBuilder()
                            .error()
                            .source("registerUser.birthday")
                            .defaultText("Age not valid. Should be digits and not more than 2 digits")
                            .build());
            status = "failure";
        }

        if (!Validate.isValidMail(register.getRegisterUser().getEmail())) {
            messageContext.addMessage(
                    new MessageBuilder()
                            .error()
                            .source("registerUser.email")
                            .defaultText("Email Address provided is not valid")
                            .build());
            status = "failure";
        }

        if (register.getRegisterUser().getEmail() != null && register.getRegisterUser().getEmail().length() <= mailLength) {
            messageContext.addMessage(
                    new MessageBuilder()
                            .error()
                            .source("registerUser.email")
                            .defaultText("Email address has to be at least of size " + mailLength + " characters")
                            .build());
            status = "failure";
        }

        if (register.getRegisterUser().getPassword().length() < passwordLength) {
            messageContext.addMessage(
                    new MessageBuilder()
                            .error()
                            .source("registerUser.password")
                            .defaultText("Password minimum length of " + passwordLength + " characters")
                            .build());
            status = "failure";
        }

        if (!register.getRegisterUser().isAcceptsAgreement()) {
            if (messageContext.getAllMessages().length > 0) {
                messageContext.addMessage(
                        new MessageBuilder()
                                .error()
                                .source("registerUser.acceptsAgreement")
                                .defaultText("To continue, please check accept to terms")
                                .build());
                status = "failure";
            } else {
                messageContext.addMessage(
                        new MessageBuilder()
                                .error()
                                .source("registerUser.acceptsAgreement")
                                .defaultText("To continue, please check accept to terms")
                                .build());
                status = "failure";
            }
        }

        LOG.info("Validate user profile signup rid={} status={}", register.getRegisterUser().getRid(), status);
        return status;
    }

    /**
     * Validate signed up user info.
     *
     * @param register
     * @param messageContext
     * @return
     */
    @SuppressWarnings ("unused")
    public String validateUserProfileDetails(Register register, MessageContext messageContext) {
        LOG.info("Validate user profile rid={}", register.getRegisterUser().getRid());
        String status = LandingController.SUCCESS;

        if (StringUtils.isBlank(register.getRegisterUser().getFirstName())) {
            messageContext.addMessage(
                    new MessageBuilder()
                            .error()
                            .source("registerUser.firstName")
                            .defaultText("First name cannot be empty")
                            .build());
            status = "failure";
        }

        if (StringUtils.isNotBlank(register.getRegisterUser().getFirstName()) && !Validate.isValidName(register.getRegisterUser().getFirstName())) {
            messageContext.addMessage(
                    new MessageBuilder()
                            .error()
                            .source("registerUser.firstName")
                            .defaultText("First name is not a valid name: " + register.getRegisterUser().getFirstName())
                            .build());
            status = "failure";
        }

        if (register.getRegisterUser().getFirstName().length() < nameLength) {
            messageContext.addMessage(
                    new MessageBuilder()
                            .error()
                            .source("registerUser.firstName")
                            .defaultText("First name minimum length of " + nameLength + " characters")
                            .build());
            status = "failure";
        }

        if (StringUtils.isBlank(register.getRegisterUser().getLastName())) {
            messageContext.addMessage(
                    new MessageBuilder()
                            .error()
                            .source("registerUser.lastName")
                            .defaultText("Last name cannot be empty")
                            .build());
            status = "failure";
        }

        if (StringUtils.isNotBlank(register.getRegisterUser().getLastName()) && !Validate.isValidName(register.getRegisterUser().getLastName())) {
            messageContext.addMessage(
                    new MessageBuilder()
                            .error()
                            .source("registerUser.lastName")
                            .defaultText("Last name is not a valid name: " + register.getRegisterUser().getLastName())
                            .build());
            status = "failure";
        }

        if (StringUtils.isBlank(register.getRegisterUser().getAddress())) {
            messageContext.addMessage(
                    new MessageBuilder()
                            .error()
                            .source("registerUser.address")
                            .defaultText("Your Address cannot be empty")
                            .build());
            status = "failure";
        }

        if (StringUtils.isBlank(register.getRegisterUser().getPhoneNotFormatted())) {
            messageContext.addMessage(
                    new MessageBuilder()
                            .error()
                            .source("registerUser.phone")
                            .defaultText("Your Phone number cannot be empty")
                            .build());
            status = "failure";
        }

        if (StringUtils.isNotBlank(register.getRegisterUser().getPhoneNotFormatted())) {
            if (!Formatter.isValidPhone(register.getRegisterUser().getPhoneNotFormatted())) {
                messageContext.addMessage(
                        new MessageBuilder()
                                .error()
                                .source("registerUser.phone")
                                .defaultText("Your Phone number '" + register.getRegisterUser().getPhoneNotFormatted() + "' is not valid")
                                .build());
                status = "failure";
            }
        }

        LOG.info("Validate user profile rid={} status={}", register.getRegisterUser().getRid(), status);
        return status;
    }
}
