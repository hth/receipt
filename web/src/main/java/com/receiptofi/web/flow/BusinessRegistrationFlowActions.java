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
    public boolean isRegistrationComplete(Register br) {
        BusinessUserEntity businessUser = businessUserService.findBusinessUser(br.getRegisterUser().getRid());
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
                LOG.error("Reached unsupported rid={} condition={}", br.getRegisterUser().getRid(), businessUser.getBusinessUserRegistrationStatus());
                throw new UnsupportedOperationException("Reached unsupported condition " + businessUser.getBusinessUserRegistrationStatus());
            case I:
            case N:
                return false;
            default:
                LOG.error("Reached unsupported rid={} condition={}", br.getRegisterUser().getRid(), businessUser.getBusinessUserRegistrationStatus());
                throw new UnsupportedOperationException("Reached unsupported condition " + businessUser.getBusinessUserRegistrationStatus());
        }
    }

    /**
     * @param br
     * @return
     * @throws BusinessRegistrationException
     */
    @SuppressWarnings ("unused")
    public Register completeRegistrationInformation(Register br, String key)
            throws BusinessRegistrationException {
        try {
            InviteEntity invite = inviteService.findByAuthenticationKey(key);
            inviteService.completeProfileForInvitationSignup(
                    br.getRegisterUser().getFirstName(),
                    br.getRegisterUser().getLastName(),
                    br.getRegisterUser().getBirthday(),
                    br.getRegisterUser().getAddress(),
                    br.getRegisterUser().getCountryShortName(),
                    br.getRegisterUser().getPhone(),
                    br.getRegisterUser().getPassword(),
                    invite
            );

            BizNameEntity bizName = bizService.findMatchingBusiness(br.getRegisterBusiness().getBusinessName());
            if (null == bizName) {
                bizName = BizNameEntity.newInstance();
                bizName.setBusinessName(br.getRegisterBusiness().getBusinessName());
            }
            bizName.setBusinessTypes(br.getRegisterBusiness().getBusinessTypes());
            bizService.saveName(bizName);

            BizStoreEntity bizStore = bizService.findMatchingStore(
                    br.getRegisterBusiness().getBusinessAddress(),
                    br.getRegisterBusiness().getBusinessPhoneNotFormatted());
            if (bizStore == null) {
                bizStore = BizStoreEntity.newInstance();
                bizStore.setBizName(bizName);
                bizStore.setPhone(br.getRegisterBusiness().getBusinessPhone());
                bizStore.setAddress(br.getRegisterBusiness().getBusinessAddress());
                validateAddress(bizStore);
                bizService.saveStore(bizStore);
            }

            BusinessUserEntity businessUser = businessUserService.findBusinessUser(br.getRegisterUser().getRid());
            if (null == businessUser) {
                businessUser = BusinessUserEntity.newInstance(br.getRegisterUser().getRid(), invite.getUserLevel());
            }
            businessUser
                    .setBizName(bizName)
                    .setBusinessUserRegistrationStatus(BusinessUserRegistrationStatusEnum.C);

            businessUserService.save(businessUser);
            br.getRegisterBusiness().setBusinessUser(businessUser);
            return br;
        } catch (Exception e) {
            LOG.error("Error updating business user profile rid={} reason={}",
                    br.getRegisterUser().getRid(), e.getLocalizedMessage(), e);
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
    public void updateProfile(Register br) {
        DecodedAddress decodedAddress = DecodedAddress.newInstance(externalService.getGeocodingResults(br.getRegisterUser().getAddress()), br.getRegisterUser().getAddress());
        if (decodedAddress.isNotEmpty()) {
            br.getRegisterUser().setAddress(decodedAddress.getFormattedAddress());
            br.getRegisterUser().setCountryShortName(decodedAddress.getCountryShortName());
        }
        br.getRegisterUser().setPhone(CommonUtil.phoneCleanup(br.getRegisterUser().getPhone()));
    }

    @SuppressWarnings ("unused")
    public void updateBusiness(Register br) {
        DecodedAddress decodedAddress = DecodedAddress.newInstance(externalService.getGeocodingResults(br.getRegisterBusiness().getBusinessAddress()), br.getRegisterBusiness().getBusinessAddress());
        if (decodedAddress.isNotEmpty()) {
            br.getRegisterBusiness().setBusinessAddress(decodedAddress.getFormattedAddress());
            br.getRegisterBusiness().setBusinessCountryShortName(decodedAddress.getCountryShortName());
        }
        br.getRegisterBusiness().setBusinessPhone(CommonUtil.phoneCleanup(br.getRegisterBusiness().getBusinessPhone()));
    }

    /**
     * Validate business user profile.
     *
     * @param br
     * @param messageContext
     * @return
     */
    @SuppressWarnings ("unused")
    public String validateUserProfileDetails(Register br, MessageContext messageContext) {
        LOG.info("Validate business user rid={}", br.getRegisterUser().getRid());
        String status = LandingController.SUCCESS;

        if (StringUtils.isBlank(br.getRegisterUser().getFirstName())) {
            messageContext.addMessage(
                    new MessageBuilder()
                            .error()
                            .source("registerUser.firstName")
                            .defaultText("First name cannot be empty")
                            .build());
            status = "failure";
        }

        if (StringUtils.isNotBlank(br.getRegisterUser().getFirstName()) && !Validate.isValidName(br.getRegisterUser().getFirstName())) {
            messageContext.addMessage(
                    new MessageBuilder()
                            .error()
                            .source("registerUser.firstName")
                            .defaultText("First name is not a valid name: " + br.getRegisterUser().getFirstName())
                            .build());
            status = "failure";
        }

        if (br.getRegisterUser().getFirstName().length() < nameLength) {
            messageContext.addMessage(
                    new MessageBuilder()
                            .error()
                            .source("registerUser.firstName")
                            .defaultText("First name minimum length of " + nameLength + " characters")
                            .build());
            status = "failure";
        }

        if (StringUtils.isBlank(br.getRegisterUser().getLastName())) {
            messageContext.addMessage(
                    new MessageBuilder()
                            .error()
                            .source("registerUser.lastName")
                            .defaultText("Last name cannot be empty")
                            .build());
            status = "failure";
        }

        if (StringUtils.isNotBlank(br.getRegisterUser().getLastName()) && !Validate.isValidName(br.getRegisterUser().getLastName())) {
            messageContext.addMessage(
                    new MessageBuilder()
                            .error()
                            .source("registerUser.lastName")
                            .defaultText("Last name is not a valid name: " + br.getRegisterUser().getLastName())
                            .build());
            status = "failure";
        }

        if (StringUtils.isBlank(br.getRegisterUser().getAddress())) {
            messageContext.addMessage(
                    new MessageBuilder()
                            .error()
                            .source("registerUser.address")
                            .defaultText("Your Address cannot be empty")
                            .build());
            status = "failure";
        }

        if (StringUtils.isBlank(br.getRegisterUser().getPhoneNotFormatted())) {
            messageContext.addMessage(
                    new MessageBuilder()
                            .error()
                            .source("registerUser.phone")
                            .defaultText("Your Phone number cannot be empty")
                            .build());
            status = "failure";
        }

        if (StringUtils.isNotBlank(br.getRegisterUser().getPhoneNotFormatted())) {
            if (!Formatter.isValidPhone(br.getRegisterUser().getPhoneNotFormatted())) {
                messageContext.addMessage(
                        new MessageBuilder()
                                .error()
                                .source("registerUser.phone")
                                .defaultText("Your Phone number '" + br.getRegisterUser().getPhoneNotFormatted() + "' is not valid")
                                .build());
                status = "failure";
            }
        }

        if (StringUtils.isNotBlank(br.getRegisterUser().getBirthday()) && !Constants.AGE_RANGE.matcher(br.getRegisterUser().getBirthday()).matches()) {
            messageContext.addMessage(
                    new MessageBuilder()
                            .error()
                            .source("registerUser.birthday")
                            .defaultText("Age not valid. Should be digits and not more than 2 digits")
                            .build());
            status = "failure";
        }

        if (!Validate.isValidMail(br.getRegisterUser().getEmail())) {
            messageContext.addMessage(
                    new MessageBuilder()
                            .error()
                            .source("registerUser.email")
                            .defaultText("Email Address provided is not valid")
                            .build());
            status = "failure";
        }

        if (br.getRegisterUser().getEmail() != null && br.getRegisterUser().getEmail().length() <= mailLength) {
            messageContext.addMessage(
                    new MessageBuilder()
                            .error()
                            .source("registerUser.email")
                            .defaultText("Email address has to be at least of size " + mailLength + " characters")
                            .build());
            status = "failure";
        }

        if (br.getRegisterUser().getPassword().length() < passwordLength) {
            messageContext.addMessage(
                    new MessageBuilder()
                            .error()
                            .source("registerUser.password")
                            .defaultText("Password minimum length of " + passwordLength + " characters")
                            .build());
            status = "failure";
        }

        if (!br.getRegisterUser().isAcceptsAgreement()) {
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

        LOG.info("Validate business user rid={} status={}", br.getRegisterUser().getRid(), status);
        return status;
    }

    /**
     * Validate business user profile.
     *
     * @param br
     * @param messageContext
     * @return
     */
    @SuppressWarnings ("unused")
    public String validateBusinessDetails(Register br, MessageContext messageContext) {
        LOG.info("Validate business rid={}", br.getRegisterUser().getRid());
        String status = "success";

        if (StringUtils.isBlank(br.getRegisterBusiness().getBusinessName())) {
            messageContext.addMessage(
                    new MessageBuilder()
                            .error()
                            .source("registerBusiness.businessName")
                            .defaultText("Business Name cannot be empty")
                            .build());
            status = "failure";
        }

        if (null == br.getRegisterBusiness().getBusinessTypes()) {
            messageContext.addMessage(
                    new MessageBuilder()
                            .error()
                            .source("registerBusiness.businessTypes")
                            .defaultText("Business Type is not selected")
                            .build());
            status = "failure";
        }

        if (StringUtils.isBlank(br.getRegisterBusiness().getBusinessAddress())) {
            messageContext.addMessage(
                    new MessageBuilder()
                            .error()
                            .source("registerBusiness.businessAddress")
                            .defaultText("Business Address cannot be empty")
                            .build());
            status = "failure";
        }

        if (StringUtils.isBlank(br.getRegisterBusiness().getBusinessPhoneNotFormatted())) {
            messageContext.addMessage(
                    new MessageBuilder()
                            .error()
                            .source("registerBusiness.businessPhone")
                            .defaultText("Business Phone cannot be Empty")
                            .build());
            status = "failure";
        }

        LOG.info("Validate business rid={} status={}", br.getRegisterUser().getRid(), status);
        return status;
    }
}
