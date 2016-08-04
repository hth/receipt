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
        MigrateToBusinessRegistration migrateToBusinessRegistration = MigrateToBusinessRegistration.newInstance(businessUser, bizStore);
        migrateToBusinessRegistration.getRegisterUser().setEmail(userProfile.getEmail())
                .setFirstName(userProfile.getFirstName())
                .setLastName(userProfile.getLastName())
                .setAddress(userProfile.getAddress())
                .setCountryShortName(userProfile.getCountryShortName())
                .setPhone(userProfile.getPhone())
                .setEmailValidated(accountService.findByReceiptUserId(rid).isAccountValidated());
        return migrateToBusinessRegistration;
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
     * @param mr
     * @return
     * @throws MigrateToBusinessRegistrationException
     */
    @SuppressWarnings ("unused")
    public Register completeRegistrationInformation(Register mr)
            throws MigrateToBusinessRegistrationException {
        try {
            updateUserProfile(mr);

            BizNameEntity bizName = bizService.findMatchingBusiness(mr.getRegisterBusiness().getBusinessName());
            if (null == bizName) {
                bizName = BizNameEntity.newInstance();
                bizName.setBusinessName(mr.getRegisterBusiness().getBusinessName());
            }
            bizName.setBusinessTypes(mr.getRegisterBusiness().getBusinessTypes());
            bizService.saveName(bizName);

            BizStoreEntity bizStore = bizService.findMatchingStore(
                    mr.getRegisterBusiness().getBusinessAddress(),
                    mr.getRegisterBusiness().getBusinessPhoneNotFormatted());
            if (bizStore == null) {
                bizStore = BizStoreEntity.newInstance();
                bizStore.setBizName(bizName);
                bizStore.setPhone(mr.getRegisterBusiness().getBusinessPhone());
                bizStore.setAddress(mr.getRegisterBusiness().getBusinessAddress());
                validateAddress(bizStore);
                bizService.saveStore(bizStore);
            }

            BusinessUserEntity businessUser = businessUserService.findBusinessUser(mr.getRegisterUser().getRid());
            businessUser
                    .setBizName(bizName)
                    .setBusinessUserRegistrationStatus(BusinessUserRegistrationStatusEnum.C);

            businessUserService.save(businessUser);
            mr.getRegisterBusiness().setBusinessUser(businessUser);
            return mr;
        } catch (Exception e) {
            LOG.error("Error updating business user profile rid={} reason={}",
                    mr.getRegisterUser().getRid(), e.getLocalizedMessage(), e);
            throw new MigrateToBusinessRegistrationException("Error updating profile", e);
        }
    }

    /**
     * Update user profile info.
     *
     * @param mr
     */
    private void updateUserProfile(Register mr) {
        UserProfileEntity userProfile = userProfilePreferenceService.findByReceiptUserId(mr.getRegisterUser().getRid());

        userProfile.setAddress(mr.getRegisterUser().getAddress());
        userProfile.setCountryShortName(mr.getRegisterUser().getCountryShortName());
        userProfile.setPhone(mr.getRegisterUser().getPhoneNotFormatted());
        userProfilePreferenceService.updateProfile(userProfile);

        if (!userProfile.getFirstName().equals(mr.getRegisterUser().getFirstName()) && !userProfile.getLastName().equals(mr.getRegisterUser().getLastName())) {
            accountService.updateName(mr.getRegisterUser().getFirstName(), mr.getRegisterUser().getLastName(), mr.getRegisterUser().getRid());
        }
    }

    @SuppressWarnings ("unused")
    public void validateAddress(BizStoreEntity bizStore) {
        if (null == bizStore.getId() || !bizStore.isValidatedUsingExternalAPI()) {
            externalService.decodeAddress(bizStore);
        }
    }

    @SuppressWarnings ("unused")
    public void updateProfile(Register mr) {
        DecodedAddress decodedAddress = DecodedAddress.newInstance(externalService.getGeocodingResults(mr.getRegisterUser().getAddress()), mr.getRegisterUser().getAddress());
        if (decodedAddress.isNotEmpty()) {
            mr.getRegisterUser().setAddress(decodedAddress.getFormattedAddress());
            mr.getRegisterUser().setCountryShortName(decodedAddress.getCountryShortName());
        }
        mr.getRegisterUser().setPhone(CommonUtil.phoneCleanup(mr.getRegisterUser().getPhone()));
    }

    @SuppressWarnings ("unused")
    public void updateBusiness(Register mr) {
        DecodedAddress decodedAddress = DecodedAddress.newInstance(externalService.getGeocodingResults(mr.getRegisterBusiness().getBusinessAddress()), mr.getRegisterBusiness().getBusinessAddress());
        if (decodedAddress.isNotEmpty()) {
            mr.getRegisterBusiness().setBusinessAddress(decodedAddress.getFormattedAddress());
            mr.getRegisterBusiness().setBusinessCountryShortName(decodedAddress.getCountryShortName());
        }
        mr.getRegisterBusiness().setBusinessPhone(CommonUtil.phoneCleanup(mr.getRegisterBusiness().getBusinessPhone()));
    }

    /**
     * Validate business user profile.
     *
     * @param mr
     * @param messageContext
     * @return
     */
    @SuppressWarnings ("unused")
    public String validateUserProfileDetails(Register mr, MessageContext messageContext) {
        LOG.info("Validate business user rid={}", mr.getRegisterUser().getRid());
        String status = LandingController.SUCCESS;

        if (StringUtils.isBlank(mr.getRegisterUser().getFirstName())) {
            messageContext.addMessage(
                    new MessageBuilder()
                            .error()
                            .source("registerUser.firstName")
                            .defaultText("First name cannot be empty")
                            .build());
            status = "failure";
        }

        if (StringUtils.isBlank(mr.getRegisterUser().getLastName())) {
            messageContext.addMessage(
                    new MessageBuilder()
                            .error()
                            .source("registerUser.lastName")
                            .defaultText("Last name cannot be empty")
                            .build());
            status = "failure";
        }

        if (StringUtils.isBlank(mr.getRegisterUser().getAddress())) {
            messageContext.addMessage(
                    new MessageBuilder()
                            .error()
                            .source("registerUser.address")
                            .defaultText("Your Address cannot be Empty")
                            .build());
            status = "failure";
        }

        if (StringUtils.isBlank(mr.getRegisterUser().getPhoneNotFormatted())) {
            messageContext.addMessage(
                    new MessageBuilder()
                            .error()
                            .source("registerUser.phone")
                            .defaultText("Your Phone cannot be Empty")
                            .build());
            status = "failure";
        }

        LOG.info("Validate business user rid={} status={}", mr.getRegisterUser().getRid(), status);
        return status;
    }

    /**
     * Validate business user profile.
     *
     * @param mr
     * @param messageContext
     * @return
     */
    @SuppressWarnings ("unused")
    public String validateBusinessDetails(Register mr, MessageContext messageContext) {
        LOG.info("Validate business rid={}", mr.getRegisterUser().getRid());
        String status = "success";

        if (StringUtils.isBlank(mr.getRegisterBusiness().getBusinessName())) {
            messageContext.addMessage(
                    new MessageBuilder()
                            .error()
                            .source("registerBusiness.businessName")
                            .defaultText("Business Name cannot be empty")
                            .build());
            status = "failure";
        }

        if (null == mr.getRegisterBusiness().getBusinessTypes()) {
            messageContext.addMessage(
                    new MessageBuilder()
                            .error()
                            .source("registerBusiness.businessTypes")
                            .defaultText("Business Type is not selected")
                            .build());
            status = "failure";
        }

        if (StringUtils.isBlank(mr.getRegisterBusiness().getBusinessAddress())) {
            messageContext.addMessage(
                    new MessageBuilder()
                            .error()
                            .source("registerBusiness.businessAddress")
                            .defaultText("Business Address cannot be empty")
                            .build());
            status = "failure";
        }

        if (StringUtils.isBlank(mr.getRegisterBusiness().getBusinessPhoneNotFormatted())) {
            messageContext.addMessage(
                    new MessageBuilder()
                            .error()
                            .source("registerBusiness.businessPhone")
                            .defaultText("Business Phone cannot be Empty")
                            .build());
            status = "failure";
        }

        LOG.info("Validate business rid={} status={}", mr.getRegisterUser().getRid(), status);
        return status;
    }
}
