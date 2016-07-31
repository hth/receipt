package com.receiptofi.web.flow;

import com.receiptofi.domain.InviteEntity;
import com.receiptofi.domain.flow.BusinessRegistration;
import com.receiptofi.domain.shared.DecodedAddress;
import com.receiptofi.service.ExternalService;
import com.receiptofi.service.InviteService;
import com.receiptofi.utils.CommonUtil;
import com.receiptofi.utils.Constants;
import com.receiptofi.utils.Formatter;
import com.receiptofi.utils.Validate;
import com.receiptofi.web.controller.access.LandingController;
import com.receiptofi.web.flow.exception.BusinessRegistrationException;

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
public class BusinessRegistrationFlowActions {
    private static final Logger LOG = LoggerFactory.getLogger(BusinessRegistrationFlowActions.class);

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
    public BusinessRegistrationFlowActions(InviteService inviteService, ExternalService externalService) {
        this.inviteService = inviteService;
        this.externalService = externalService;
    }

    @SuppressWarnings ("unused")
    public BusinessRegistration findInvite(String key) throws BusinessRegistrationException {
        if (StringUtils.isBlank(key)) {
            LOG.error("Authorization key is missing");
            throw new BusinessRegistrationException("Authorization key is missing");
        }
        InviteEntity invite = inviteService.findByAuthenticationKey(key);
        if (null == invite) {
            LOG.error("Authorization key is used or is incorrect={}", key);
            throw new BusinessRegistrationException("Authorization key is used or is incorrect");
        }

        return BusinessRegistration.newInstance(invite, registrationTurnedOn);
    }

    @SuppressWarnings ("unused")
    public void updateProfile(BusinessRegistration businessRegistration) {
        DecodedAddress decodedAddress = DecodedAddress.newInstance(externalService.getGeocodingResults(businessRegistration.getAddress()), businessRegistration.getAddress());
        if (decodedAddress.isNotEmpty()) {
            businessRegistration.setAddress(decodedAddress.getFormattedAddress());
            businessRegistration.setCountryShortName(decodedAddress.getCountryShortName());
        }
        businessRegistration.setPhone(CommonUtil.phoneCleanup(businessRegistration.getPhone()));
    }

    @SuppressWarnings ("unused")
    public void updateBusiness(BusinessRegistration businessRegistration) {
        DecodedAddress decodedAddress = DecodedAddress.newInstance(externalService.getGeocodingResults(businessRegistration.getBusinessAddress()), businessRegistration.getAddress());
        if (decodedAddress.isNotEmpty()) {
            businessRegistration.setBusinessAddress(decodedAddress.getFormattedAddress());
            businessRegistration.setBusinessCountryShortName(decodedAddress.getCountryShortName());
        }
        businessRegistration.setBusinessPhone(CommonUtil.phoneCleanup(businessRegistration.getBusinessPhone()));
    }



    /**
     * Validate business user profile.
     *
     * @param businessRegistration
     * @param messageContext
     * @return
     */
    @SuppressWarnings ("unused")
    public String validateUserProfileDetails(BusinessRegistration businessRegistration, MessageContext messageContext) {
        LOG.info("Validate business user rid={}", businessRegistration.getRid());
        String status = LandingController.SUCCESS;

        if (StringUtils.isBlank(businessRegistration.getFirstName())) {
            messageContext.addMessage(
                    new MessageBuilder()
                            .error()
                            .source("firstName")
                            .defaultText("First name cannot be empty")
                            .build());
            status = "failure";
        }

        if (StringUtils.isNotBlank(businessRegistration.getFirstName()) && !Validate.isValidName(businessRegistration.getFirstName())) {
            messageContext.addMessage(
                    new MessageBuilder()
                            .error()
                            .source("firstName")
                            .defaultText("First name is not a valid name: " + businessRegistration.getFirstName())
                            .build());
            status = "failure";
        }

        if (businessRegistration.getFirstName().length() < nameLength) {
            messageContext.addMessage(
                    new MessageBuilder()
                            .error()
                            .source("firstName")
                            .defaultText("First name minimum length of " + nameLength + " characters")
                            .build());
            status = "failure";
        }

        if (StringUtils.isBlank(businessRegistration.getLastName())) {
            messageContext.addMessage(
                    new MessageBuilder()
                            .error()
                            .source("lastName")
                            .defaultText("Last name cannot be empty")
                            .build());
            status = "failure";
        }

        if (StringUtils.isNotBlank(businessRegistration.getLastName()) && !Validate.isValidName(businessRegistration.getLastName())) {
            messageContext.addMessage(
                    new MessageBuilder()
                            .error()
                            .source("lastName")
                            .defaultText("Last name is not a valid name: " + businessRegistration.getLastName())
                            .build());
            status = "failure";
        }

        if (StringUtils.isBlank(businessRegistration.getAddress())) {
            messageContext.addMessage(
                    new MessageBuilder()
                            .error()
                            .source("address")
                            .defaultText("Your Address cannot be empty")
                            .build());
            status = "failure";
        }

        if (StringUtils.isBlank(businessRegistration.getPhoneNotFormatted())) {
            messageContext.addMessage(
                    new MessageBuilder()
                            .error()
                            .source("phone")
                            .defaultText("Your Phone number cannot be empty")
                            .build());
            status = "failure";
        }

        if (StringUtils.isNotBlank(businessRegistration.getPhoneNotFormatted())) {
            if (!Formatter.isValidPhone(businessRegistration.getPhoneNotFormatted())) {
                messageContext.addMessage(
                        new MessageBuilder()
                                .error()
                                .source("phone")
                                .defaultText("Your Phone number '" + businessRegistration.getPhoneNotFormatted() + "' is not valid")
                                .build());
                status = "failure";
            }
        }

        if (StringUtils.isNotBlank(businessRegistration.getBirthday()) && !Constants.AGE_RANGE.matcher(businessRegistration.getBirthday()).matches()) {
            messageContext.addMessage(
                    new MessageBuilder()
                            .error()
                            .source("birthday")
                            .defaultText("Age not valid. Should be digits and not more than 2 digits")
                            .build());
            status = "failure";
        }

        if (!Validate.isValidMail(businessRegistration.getEmail())) {
            messageContext.addMessage(
                    new MessageBuilder()
                            .error()
                            .source("email")
                            .defaultText("Email Address provided is not valid")
                            .build());
            status = "failure";
        }

        if (businessRegistration.getEmail() != null && businessRegistration.getEmail().length() <= mailLength) {
            messageContext.addMessage(
                    new MessageBuilder()
                            .error()
                            .source("email")
                            .defaultText("Email address has to be at least of size " + mailLength + " characters")
                            .build());
            status = "failure";
        }

        if (businessRegistration.getPassword().length() < passwordLength) {
            messageContext.addMessage(
                    new MessageBuilder()
                            .error()
                            .source("password")
                            .defaultText("Password minimum length of " + passwordLength + " characters")
                            .build());
            status = "failure";
        }

        if (!businessRegistration.isAcceptsAgreement()) {
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

        LOG.info("Validate business user rid={} status={}", businessRegistration.getRid(), status);
        return status;
    }

    /**
     * Validate business user profile.
     *
     * @param businessRegistration
     * @param messageContext
     * @return
     */
    @SuppressWarnings ("unused")
    public String validateBusinessDetails(BusinessRegistration businessRegistration, MessageContext messageContext) {
        LOG.info("Validate business rid={}", businessRegistration.getRid());
        String status = "success";

        if (StringUtils.isBlank(businessRegistration.getBusinessName())) {
            messageContext.addMessage(
                    new MessageBuilder()
                            .error()
                            .source("businessName")
                            .defaultText("Business Name cannot be empty")
                            .build());
            status = "failure";
        }

        if (null == businessRegistration.getBusinessTypes()) {
            messageContext.addMessage(
                    new MessageBuilder()
                            .error()
                            .source("businessTypes")
                            .defaultText("Business Type is not selected")
                            .build());
            status = "failure";
        }

        if (StringUtils.isBlank(businessRegistration.getBusinessAddress())) {
            messageContext.addMessage(
                    new MessageBuilder()
                            .error()
                            .source("businessAddress")
                            .defaultText("Business Address cannot be empty")
                            .build());
            status = "failure";
        }

        if (StringUtils.isBlank(businessRegistration.getBusinessPhoneNotFormatted())) {
            messageContext.addMessage(
                    new MessageBuilder()
                            .error()
                            .source("businessPhone")
                            .defaultText("Business Phone cannot be Empty")
                            .build());
            status = "failure";
        }

        LOG.info("Validate business rid={} status={}", businessRegistration.getRid(), status);
        return status;
    }
}
