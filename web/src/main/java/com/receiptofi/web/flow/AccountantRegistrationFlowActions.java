package com.receiptofi.web.flow;

import com.receiptofi.domain.InviteEntity;
import com.receiptofi.domain.flow.AccountantRegistration;
import com.receiptofi.domain.shared.DecodedAddress;
import com.receiptofi.service.ExternalService;
import com.receiptofi.service.InviteService;
import com.receiptofi.utils.CommonUtil;
import com.receiptofi.utils.Constants;
import com.receiptofi.utils.Formatter;
import com.receiptofi.utils.Validate;
import com.receiptofi.web.controller.access.LandingController;
import com.receiptofi.web.flow.exception.AccountantRegistrationException;

import org.apache.commons.lang3.StringUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.binding.message.MessageBuilder;
import org.springframework.binding.message.MessageContext;
import org.springframework.stereotype.Component;

/**
 * User: hitender
 * Date: 7/27/16 4:04 PM
 */
@Component
public class AccountantRegistrationFlowActions {
    private static final Logger LOG = LoggerFactory.getLogger(AccountantRegistrationFlowActions.class);

    private InviteService inviteService;
    private ExternalService externalService;

    @Value ("${registration.turned.on}")
    private boolean registrationTurnedOn;

    @Value ("${AccountRegistrationController.mailLength}")
    private int mailLength;

    @Value ("${AccountRegistrationController.nameLength}")
    private int nameLength;

    @Value ("${AccountRegistrationController.passwordLength}")
    private int passwordLength;

    @SuppressWarnings ("all")
    @Autowired
    public AccountantRegistrationFlowActions(InviteService inviteService, ExternalService externalService) {
        this.inviteService = inviteService;
        this.externalService = externalService;
    }

    @SuppressWarnings ("unused")
    public AccountantRegistration findInvite(String key) throws AccountantRegistrationException {
        if (StringUtils.isBlank(key)) {
            LOG.error("Authorization key is missing");
            throw new AccountantRegistrationException("Authorization key is missing");
        }
        InviteEntity invite = inviteService.findByAuthenticationKey(key);
        if (null == invite) {
            LOG.error("Authorization key is used or is incorrect={}", key);
            throw new AccountantRegistrationException("Authorization key is used or is incorrect");
        }

        return AccountantRegistration.newInstance(invite, registrationTurnedOn);
    }

    @SuppressWarnings ("unused")
    public void updateProfile(AccountantRegistration accountantRegistration) {
        DecodedAddress decodedAddress = DecodedAddress.newInstance(externalService.getGeocodingResults(accountantRegistration.getAddress()), accountantRegistration.getAddress());
        if (decodedAddress.isNotEmpty()) {
            accountantRegistration.setAddress(decodedAddress.getFormattedAddress());
            accountantRegistration.setCountryShortName(decodedAddress.getCountryShortName());
        }
        accountantRegistration.setPhone(CommonUtil.phoneCleanup(accountantRegistration.getPhone()));
    }


    /**
     * Validate business user profile.
     *
     * @param accountantRegistration
     * @param messageContext
     * @return
     */
    @SuppressWarnings ("unused")
    public String validateUserProfileDetails(AccountantRegistration accountantRegistration, MessageContext messageContext) {
        LOG.info("Validate business user rid={}", accountantRegistration.getRid());
        String status = LandingController.SUCCESS;

        if (StringUtils.isBlank(accountantRegistration.getFirstName())) {
            messageContext.addMessage(
                    new MessageBuilder()
                            .error()
                            .source("firstName")
                            .defaultText("First name cannot be empty")
                            .build());
            status = "failure";
        }

        if (StringUtils.isNotBlank(accountantRegistration.getFirstName()) && !Validate.isValidName(accountantRegistration.getFirstName())) {
            messageContext.addMessage(
                    new MessageBuilder()
                            .error()
                            .source("firstName")
                            .defaultText("First name is not a valid name: " + accountantRegistration.getFirstName())
                            .build());
            status = "failure";
        }

        if (accountantRegistration.getFirstName().length() < nameLength) {
            messageContext.addMessage(
                    new MessageBuilder()
                            .error()
                            .source("firstName")
                            .defaultText("First name minimum length of " + nameLength + " characters")
                            .build());
            status = "failure";
        }

        if (StringUtils.isBlank(accountantRegistration.getLastName())) {
            messageContext.addMessage(
                    new MessageBuilder()
                            .error()
                            .source("lastName")
                            .defaultText("Last name cannot be empty")
                            .build());
            status = "failure";
        }

        if (StringUtils.isNotBlank(accountantRegistration.getLastName()) && !Validate.isValidName(accountantRegistration.getLastName())) {
            messageContext.addMessage(
                    new MessageBuilder()
                            .error()
                            .source("lastName")
                            .defaultText("Last name is not a valid name: " + accountantRegistration.getLastName())
                            .build());
            status = "failure";
        }

        if (StringUtils.isBlank(accountantRegistration.getAddress())) {
            messageContext.addMessage(
                    new MessageBuilder()
                            .error()
                            .source("address")
                            .defaultText("Your Address cannot be empty")
                            .build());
            status = "failure";
        }

        if (StringUtils.isBlank(accountantRegistration.getPhoneNotFormatted())) {
            messageContext.addMessage(
                    new MessageBuilder()
                            .error()
                            .source("phone")
                            .defaultText("Your Phone number cannot be empty")
                            .build());
            status = "failure";
        }

        if (StringUtils.isNotBlank(accountantRegistration.getPhoneNotFormatted())) {
            boolean isValid = Formatter.isValidPhone(accountantRegistration.getPhoneNotFormatted());
            messageContext.addMessage(
                    new MessageBuilder()
                            .error()
                            .source("phone")
                            .defaultText("Your Phone number '" + accountantRegistration.getPhoneNotFormatted() + "' is not valid")
                            .build());
            status = "failure";
        }

        if (StringUtils.isNotBlank(accountantRegistration.getBirthday()) && !Constants.AGE_RANGE.matcher(accountantRegistration.getBirthday()).matches()) {
            messageContext.addMessage(
                    new MessageBuilder()
                            .error()
                            .source("birthday")
                            .defaultText("Age not valid. Should be digits and not more than 2 digits")
                            .build());
            status = "failure";
        }

        if (!Validate.isValidMail(accountantRegistration.getEmail())) {
            messageContext.addMessage(
                    new MessageBuilder()
                            .error()
                            .source("email")
                            .defaultText("Email Address provided is not valid")
                            .build());
            status = "failure";
        }

        if (accountantRegistration.getEmail() != null && accountantRegistration.getEmail().length() <= mailLength) {
            messageContext.addMessage(
                    new MessageBuilder()
                            .error()
                            .source("email")
                            .defaultText("Email address has to be at least of size " + mailLength + " characters")
                            .build());
            status = "failure";
        }

        if (accountantRegistration.getPassword().length() < passwordLength) {
            messageContext.addMessage(
                    new MessageBuilder()
                            .error()
                            .source("password")
                            .defaultText("Password minimum length of " + passwordLength + " characters")
                            .build());
            status = "failure";
        }

        if (!accountantRegistration.isAcceptsAgreement()) {
            if (messageContext.getAllMessages().length > 0) {
                messageContext.addMessage(
                        new MessageBuilder()
                                .error()
                                .source("acceptsAgreement")
                                .defaultText("To continue, please check accept to terms")
                                .build());
                status = "failure";
            } else {
                messageContext.addMessage(
                        new MessageBuilder()
                                .error()
                                .source("acceptsAgreement")
                                .defaultText("To continue, please check accept to terms")
                                .build());
                status = "failure";
            }
        }

        LOG.info("Validate business user rid={} status={}", accountantRegistration.getRid(), status);
        return status;
    }
}
