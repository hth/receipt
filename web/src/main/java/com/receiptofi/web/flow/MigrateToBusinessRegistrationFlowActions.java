package com.receiptofi.web.flow;

import com.receiptofi.domain.BizNameEntity;
import com.receiptofi.domain.BizStoreEntity;
import com.receiptofi.domain.BusinessUserEntity;
import com.receiptofi.domain.UserProfileEntity;
import com.receiptofi.domain.flow.MigrateToBusinessRegistration;
import com.receiptofi.domain.flow.Register;
import com.receiptofi.domain.shared.DecodedAddress;
import com.receiptofi.domain.site.ReceiptUser;
import com.receiptofi.domain.types.BusinessUserRegistrationStatusEnum;
import com.receiptofi.service.AccountService;
import com.receiptofi.service.BizService;
import com.receiptofi.service.BusinessUserService;
import com.receiptofi.service.ExternalService;
import com.receiptofi.service.FetcherService;
import com.receiptofi.service.UserProfilePreferenceService;
import com.receiptofi.utils.CommonUtil;
import com.receiptofi.web.controller.access.LandingController;
import com.receiptofi.web.flow.exception.MigrateToBusinessRegistrationException;

import org.apache.commons.lang3.StringUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.binding.message.MessageBuilder;
import org.springframework.binding.message.MessageContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.Set;

/**
 * Helps migrate account to business account with correct Business Level.
 * User: hitender
 * Date: 5/20/16 9:51 PM
 */
@Component
public class MigrateToBusinessRegistrationFlowActions {
    private static final Logger LOG = LoggerFactory.getLogger(MigrateToBusinessRegistrationFlowActions.class);

    private FetcherService fetcherService;
    private UserProfilePreferenceService userProfilePreferenceService;
    private AccountService accountService;
    private BusinessUserService businessUserService;
    private BizService bizService;
    private ExternalService externalService;

    @SuppressWarnings ("all")
    @Autowired
    public MigrateToBusinessRegistrationFlowActions(
            FetcherService fetcherService,
            UserProfilePreferenceService userProfilePreferenceService,
            AccountService accountService,
            BusinessUserService businessUserService,
            BizService bizService,
            ExternalService externalService) {
        this.fetcherService = fetcherService;
        this.userProfilePreferenceService = userProfilePreferenceService;
        this.accountService = accountService;
        this.businessUserService = businessUserService;
        this.bizService = bizService;
        this.externalService = externalService;
    }

    public Set<String> findDistinctBizName(String bizName) {
        return fetcherService.findDistinctBizName(bizName);
    }

    @SuppressWarnings ("unused")
    public Register createBusinessRegistration() {
        ReceiptUser receiptUser = (ReceiptUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String rid = receiptUser.getRid();

        BusinessUserEntity businessUser = businessUserService.findBusinessUser(rid);
        BizStoreEntity bizStore = null;
        if (businessUser.getBizName() != null) {
            bizStore = bizService.findOneBizStore(businessUser.getBizName().getId());
        }
        UserProfileEntity userProfile = userProfilePreferenceService.findByReceiptUserId(rid);
        Register register = MigrateToBusinessRegistration.newInstance(businessUser, bizStore);
        register.getRegisterUser().setEmail(userProfile.getEmail())
                .setFirstName(userProfile.getFirstName())
                .setLastName(userProfile.getLastName())
                .setAddress(userProfile.getAddress())
                .setCountryShortName(userProfile.getCountryShortName())
                .setPhone(userProfile.getPhone())
                .setEmailValidated(accountService.findByReceiptUserId(rid).isAccountValidated());
        return register;
    }

    @SuppressWarnings ("unused")
    public boolean isRegistrationComplete(Register register) {
        switch (register.getRegisterBusiness().getBusinessUser().getBusinessUserRegistrationStatus()) {
            case C:
                return true;
            case I:
            case N:
                return false;
            default:
                LOG.error("Reached unsupported rid={} condition={}", register.getRegisterUser().getRid(), register.getRegisterBusiness().getBusinessUser().getBusinessUserRegistrationStatus());
                throw new UnsupportedOperationException("Reached unsupported condition " + register.getRegisterBusiness().getBusinessUser().getBusinessUserRegistrationStatus());
        }
    }

    /**
     * @param register
     * @return
     * @throws MigrateToBusinessRegistrationException
     */
    @SuppressWarnings ("unused")
    public Register completeRegistrationInformation(Register register)
            throws MigrateToBusinessRegistrationException {
        try {
            updateUserProfile(register);

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

    /**
     * Update user profile info.
     *
     * @param register
     */
    private void updateUserProfile(Register register) {
        UserProfileEntity userProfile = userProfilePreferenceService.findByReceiptUserId(register.getRegisterUser().getRid());

        userProfile.setAddress(register.getRegisterUser().getAddress());
        userProfile.setCountryShortName(register.getRegisterUser().getCountryShortName());
        userProfile.setPhone(register.getRegisterUser().getPhoneNotFormatted());
        userProfilePreferenceService.updateProfile(userProfile);

        if (!userProfile.getFirstName().equals(register.getRegisterUser().getFirstName()) && !userProfile.getLastName().equals(register.getRegisterUser().getLastName())) {
            accountService.updateName(register.getRegisterUser().getFirstName(), register.getRegisterUser().getLastName(), register.getRegisterUser().getRid());
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

        if (StringUtils.isBlank(register.getRegisterUser().getLastName())) {
            messageContext.addMessage(
                    new MessageBuilder()
                            .error()
                            .source("registerUser.lastName")
                            .defaultText("Last name cannot be empty")
                            .build());
            status = "failure";
        }

        if (StringUtils.isBlank(register.getRegisterUser().getAddress())) {
            messageContext.addMessage(
                    new MessageBuilder()
                            .error()
                            .source("registerUser.address")
                            .defaultText("Your Address cannot be Empty")
                            .build());
            status = "failure";
        }

        if (StringUtils.isBlank(register.getRegisterUser().getPhoneNotFormatted())) {
            messageContext.addMessage(
                    new MessageBuilder()
                            .error()
                            .source("registerUser.phone")
                            .defaultText("Your Phone cannot be Empty")
                            .build());
            status = "failure";
        }

        LOG.info("Validate business user rid={} status={}", register.getRegisterUser().getRid(), status);
        return status;
    }
}
