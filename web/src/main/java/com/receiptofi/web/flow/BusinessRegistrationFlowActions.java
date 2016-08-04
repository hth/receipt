package com.receiptofi.web.flow;

import com.receiptofi.domain.BizNameEntity;
import com.receiptofi.domain.BizStoreEntity;
import com.receiptofi.domain.BusinessUserEntity;
import com.receiptofi.domain.InviteEntity;
import com.receiptofi.domain.flow.BusinessRegistration;
import com.receiptofi.domain.flow.Register;
import com.receiptofi.domain.shared.DecodedAddress;
import com.receiptofi.domain.types.BusinessUserRegistrationStatusEnum;
import com.receiptofi.service.BizService;
import com.receiptofi.service.BusinessUserService;
import com.receiptofi.service.ExternalService;
import com.receiptofi.service.InviteService;
import com.receiptofi.utils.CommonUtil;
import com.receiptofi.utils.Constants;
import com.receiptofi.utils.Formatter;
import com.receiptofi.utils.Validate;
import com.receiptofi.web.controller.access.LandingController;
import com.receiptofi.web.flow.exception.BusinessRegistrationException;
import com.receiptofi.web.flow.exception.MigrateToBusinessRegistrationException;

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
    private BusinessUserService businessUserService;
    private BizService bizService;

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
    public BusinessRegistrationFlowActions(
            InviteService inviteService,
            ExternalService externalService,
            BusinessUserService businessUserService,
            BizService bizService) {
        this.inviteService = inviteService;
        this.externalService = externalService;
        this.businessUserService = businessUserService;
        this.bizService = bizService;
    }

    @SuppressWarnings ("unused")
    public Register findInvite(String key) throws BusinessRegistrationException {
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
    public boolean isRegistrationComplete(Register register) {
        BusinessUserEntity businessUser = businessUserService.findBusinessUser(register.getRegisterUser().getRid());
        if (businessUser == null) {
            return false;
        }

        switch (businessUser.getBusinessUserRegistrationStatus()) {
            case C:
                /**
                 * Likelihood of this happening is zero. Because if its approved, they would never land here.
                 * Once the registration is complete, invite is marked as inactive and it can't be re-used.
                 * Even 'V' condition would not make the user land for registration.
                 */
                LOG.error("Reached unsupported rid={} condition={}", register.getRegisterUser().getRid(), businessUser.getBusinessUserRegistrationStatus());
                throw new UnsupportedOperationException("Reached unsupported condition " + businessUser.getBusinessUserRegistrationStatus());
            case I:
            case N:
                return false;
            default:
                LOG.error("Reached unsupported rid={} condition={}", register.getRegisterUser().getRid(), businessUser.getBusinessUserRegistrationStatus());
                throw new UnsupportedOperationException("Reached unsupported condition " + businessUser.getBusinessUserRegistrationStatus());
        }
    }

    /**
     * @param register
     * @return
     * @throws BusinessRegistrationException
     */
    @SuppressWarnings ("unused")
    public Register completeRegistrationInformation(Register register, String key)
            throws BusinessRegistrationException {
        try {
            InviteEntity invite = inviteService.findByAuthenticationKey(key);
            inviteService.completeProfileForInvitationSignup(
                    register.getRegisterUser().getFirstName(),
                    register.getRegisterUser().getLastName(),
                    register.getRegisterUser().getBirthday(),
                    register.getRegisterUser().getAddress(),
                    register.getRegisterUser().getCountryShortName(),
                    register.getRegisterUser().getPhone(),
                    register.getRegisterUser().getPassword(),
                    invite
            );

            BizNameEntity bizName = bizService.findMatchingBusiness(register.getRegisterBusiness().getName());
            if (null == bizName) {
                bizName = BizNameEntity.newInstance();
                bizName.setBusinessName(register.getRegisterBusiness().getName());
            }
            bizName.setBusinessTypes(register.getRegisterBusiness().getBusinessTypes());
            bizService.saveName(bizName);

            BizStoreEntity bizStore = bizService.findMatchingStore(
                    register.getRegisterBusiness().getAddress(),
                    register.getRegisterBusiness().getBusinessPhoneNotFormatted());
            if (bizStore == null) {
                bizStore = BizStoreEntity.newInstance();
                bizStore.setBizName(bizName);
                bizStore.setPhone(register.getRegisterBusiness().getPhone());
                bizStore.setAddress(register.getRegisterBusiness().getAddress());
                validateAddress(bizStore);
                bizService.saveStore(bizStore);
            }

            BusinessUserEntity businessUser = businessUserService.findBusinessUser(register.getRegisterUser().getRid());
            if (null == businessUser) {
                businessUser = BusinessUserEntity.newInstance(register.getRegisterUser().getRid(), invite.getUserLevel());
            }
            businessUser
                    .setBizName(bizName)
                    .setBusinessUserRegistrationStatus(BusinessUserRegistrationStatusEnum.C);

            businessUserService.save(businessUser);
            register.getRegisterBusiness().setBusinessUser(businessUser);
            return register;
        } catch (Exception e) {
            LOG.error("Error updating business user profile rid={} reason={}",
                    register.getRegisterUser().getRid(), e.getLocalizedMessage(), e);
            throw new MigrateToBusinessRegistrationException("Error updating profile", e);
        }
    }

    @SuppressWarnings ("unused")
    public void validateAddress(BizStoreEntity bizStore) {
        if (null == bizStore.getId() || !bizStore.isValidatedUsingExternalAPI()) {
            externalService.decodeAddress(bizStore);
        }
    }

    @SuppressWarnings ("unused")
    public void updateProfile(Register register) {
        DecodedAddress decodedAddress = DecodedAddress.newInstance(externalService.getGeocodingResults(register.getRegisterUser().getAddress()), register.getRegisterUser().getAddress());
        if (decodedAddress.isNotEmpty()) {
            register.getRegisterUser().setAddress(decodedAddress.getFormattedAddress());
            register.getRegisterUser().setCountryShortName(decodedAddress.getCountryShortName());
        }
        register.getRegisterUser().setPhone(CommonUtil.phoneCleanup(register.getRegisterUser().getPhone()));
    }

    @SuppressWarnings ("unused")
    public void updateBusiness(Register register) {
        DecodedAddress decodedAddress = DecodedAddress.newInstance(externalService.getGeocodingResults(register.getRegisterBusiness().getAddress()), register.getRegisterBusiness().getAddress());
        if (decodedAddress.isNotEmpty()) {
            register.getRegisterBusiness().setAddress(decodedAddress.getFormattedAddress());
            register.getRegisterBusiness().setCountryShortName(decodedAddress.getCountryShortName());
        }
        register.getRegisterBusiness().setPhone(CommonUtil.phoneCleanup(register.getRegisterBusiness().getPhone()));
    }

    /**
     * Validate business user profile.
     *
     * @param register
     * @param messageContext
     * @return
     */
    @SuppressWarnings ("unused")
    public String validateUserProfileDetails(Register register, MessageContext messageContext) {
        LOG.info("Validate business user rid={}", register.getRegisterUser().getRid());
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

        LOG.info("Validate business user rid={} status={}", register.getRegisterUser().getRid(), status);
        return status;
    }

    /**
     * Validate business user profile.
     *
     * @param register
     * @param messageContext
     * @return
     */
    @SuppressWarnings ("unused")
    public String validateBusinessDetails(Register register, MessageContext messageContext) {
        LOG.info("Validate business rid={}", register.getRegisterUser().getRid());
        String status = "success";

        if (StringUtils.isBlank(register.getRegisterBusiness().getName())) {
            messageContext.addMessage(
                    new MessageBuilder()
                            .error()
                            .source("registerBusiness.businessName")
                            .defaultText("Business Name cannot be empty")
                            .build());
            status = "failure";
        }

        if (null == register.getRegisterBusiness().getBusinessTypes()) {
            messageContext.addMessage(
                    new MessageBuilder()
                            .error()
                            .source("registerBusiness.businessTypes")
                            .defaultText("Business Type is not selected")
                            .build());
            status = "failure";
        }

        if (StringUtils.isBlank(register.getRegisterBusiness().getAddress())) {
            messageContext.addMessage(
                    new MessageBuilder()
                            .error()
                            .source("registerBusiness.businessAddress")
                            .defaultText("Business Address cannot be empty")
                            .build());
            status = "failure";
        }

        if (StringUtils.isBlank(register.getRegisterBusiness().getBusinessPhoneNotFormatted())) {
            messageContext.addMessage(
                    new MessageBuilder()
                            .error()
                            .source("registerBusiness.businessPhone")
                            .defaultText("Business Phone cannot be Empty")
                            .build());
            status = "failure";
        }

        LOG.info("Validate business rid={} status={}", register.getRegisterUser().getRid(), status);
        return status;
    }
}
