package com.receiptofi.web.flow;

import com.receiptofi.domain.BizNameEntity;
import com.receiptofi.domain.BizStoreEntity;
import com.receiptofi.domain.BusinessUserEntity;
import com.receiptofi.domain.InviteEntity;
import com.receiptofi.domain.flow.BusinessRegistration;
import com.receiptofi.domain.shared.DecodedAddress;
import com.receiptofi.domain.types.BusinessUserRegistrationStatusEnum;
import com.receiptofi.service.AccountService;
import com.receiptofi.service.BizService;
import com.receiptofi.service.BusinessUserService;
import com.receiptofi.service.ExternalService;
import com.receiptofi.service.InviteService;
import com.receiptofi.service.UserProfilePreferenceService;
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
    private UserProfilePreferenceService userProfilePreferenceService;
    private AccountService accountService;

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
            BizService bizService,
            UserProfilePreferenceService userProfilePreferenceService,
            AccountService accountService) {
        this.inviteService = inviteService;
        this.externalService = externalService;
        this.businessUserService = businessUserService;
        this.bizService = bizService;
        this.userProfilePreferenceService = userProfilePreferenceService;
        this.accountService = accountService;
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
    public boolean isRegistrationComplete(BusinessRegistration br) {
        BusinessUserEntity businessUser = businessUserService.findBusinessUser(br.getRid());
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
                LOG.error("Reached unsupported rid={} condition={}", br.getRid(), businessUser.getBusinessUserRegistrationStatus());
                throw new UnsupportedOperationException("Reached unsupported condition " + businessUser.getBusinessUserRegistrationStatus());
            case I:
            case N:
                return false;
            default:
                LOG.error("Reached unsupported rid={} condition={}", br.getRid(), businessUser.getBusinessUserRegistrationStatus());
                throw new UnsupportedOperationException("Reached unsupported condition " + businessUser.getBusinessUserRegistrationStatus());
        }
    }

    /**
     * @param br
     * @return
     * @throws BusinessRegistrationException
     */
    @SuppressWarnings ("unused")
    public BusinessRegistration completeRegistrationInformation(BusinessRegistration br, String key)
            throws BusinessRegistrationException {
        try {
            InviteEntity invite = inviteService.findByAuthenticationKey(key);
            inviteService.completeProfileForInvitationSignup(
                    br.getFirstName(),
                    br.getLastName(),
                    br.getBirthday(),
                    br.getAddress(),
                    br.getCountryShortName(),
                    br.getPhone(),
                    br.getPassword(),
                    invite
            );

            BizNameEntity bizName = bizService.findMatchingBusiness(br.getBusinessName());
            if (null == bizName) {
                bizName = BizNameEntity.newInstance();
                bizName.setBusinessName(br.getBusinessName());
            }
            bizName.setBusinessTypes(br.getBusinessTypes());
            bizService.saveName(bizName);

            BizStoreEntity bizStore = bizService.findMatchingStore(
                    br.getBusinessAddress(),
                    br.getBusinessPhoneNotFormatted());
            if (bizStore == null) {
                bizStore = BizStoreEntity.newInstance();
                bizStore.setBizName(bizName);
                bizStore.setPhone(br.getBusinessPhone());
                bizStore.setAddress(br.getBusinessAddress());
                validateAddress(bizStore);
                bizService.saveStore(bizStore);
            }

            BusinessUserEntity businessUser = businessUserService.findBusinessUser(br.getRid());
            if (null == businessUser) {
                businessUser = BusinessUserEntity.newInstance(br.getRid(), invite.getUserLevel());
            }
            businessUser
                    .setBizName(bizName)
                    .setBusinessUserRegistrationStatus(BusinessUserRegistrationStatusEnum.C);

            businessUserService.save(businessUser);
            br.setBusinessUser(businessUser);
            return br;
        } catch (Exception e) {
            LOG.error("Error updating business user profile rid={} reason={}",
                    br.getRid(), e.getLocalizedMessage(), e);
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
    public void updateProfile(BusinessRegistration br) {
        DecodedAddress decodedAddress = DecodedAddress.newInstance(externalService.getGeocodingResults(br.getAddress()), br.getAddress());
        if (decodedAddress.isNotEmpty()) {
            br.setAddress(decodedAddress.getFormattedAddress());
            br.setCountryShortName(decodedAddress.getCountryShortName());
        }
        br.setPhone(CommonUtil.phoneCleanup(br.getPhone()));
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
