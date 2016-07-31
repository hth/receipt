package com.receiptofi.web.flow;

import com.receiptofi.domain.BizNameEntity;
import com.receiptofi.domain.BizStoreEntity;
import com.receiptofi.domain.BusinessUserEntity;
import com.receiptofi.domain.UserProfileEntity;
import com.receiptofi.domain.flow.MigrateBusinessRegistration;
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
    public MigrateBusinessRegistration createBusinessRegistration() {
        ReceiptUser receiptUser = (ReceiptUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String rid = receiptUser.getRid();

        BusinessUserEntity businessUser = businessUserService.findBusinessUser(rid);
        BizStoreEntity bizStore = null;
        if (businessUser.getBizName() != null) {
            bizStore = bizService.findOneBizStore(businessUser.getBizName().getId());
        }
        UserProfileEntity userProfile = userProfilePreferenceService.findByReceiptUserId(rid);
        return MigrateBusinessRegistration.newInstance(businessUser, bizStore)
                .setEmail(userProfile.getEmail())
                .setFirstName(userProfile.getFirstName())
                .setLastName(userProfile.getLastName())
                .setAddress(userProfile.getAddress())
                .setCountryShortName(userProfile.getCountryShortName())
                .setPhone(userProfile.getPhone())
                .setEmailValidated(accountService.findByReceiptUserId(rid).isAccountValidated());
    }

    @SuppressWarnings ("unused")
    public boolean isRegistrationComplete(String rid, BusinessUserEntity businessUser) {
        switch (businessUser.getBusinessUserRegistrationStatus()) {
            case C:
                return true;
            case I:
            case N:
                return false;
            default:
                LOG.error("Reached unsupported rid={} condition={}", rid, businessUser.getBusinessUserRegistrationStatus());
                throw new UnsupportedOperationException("Reached unsupported condition " + businessUser.getBusinessUserRegistrationStatus());
        }
    }

    /**
     * @param migrateBusinessRegistration
     * @return
     * @throws MigrateToBusinessRegistrationException
     */
    @SuppressWarnings ("unused")
    public MigrateBusinessRegistration completeRegistrationInformation(MigrateBusinessRegistration migrateBusinessRegistration)
            throws MigrateToBusinessRegistrationException {
        try {
            updateUserProfile(migrateBusinessRegistration);

            BizNameEntity bizName = bizService.findMatchingBusiness(migrateBusinessRegistration.getBusinessName());
            if (null == bizName) {
                bizName = BizNameEntity.newInstance();
                bizName.setBusinessName(migrateBusinessRegistration.getBusinessName());
            }
            bizName.setBusinessTypes(migrateBusinessRegistration.getBusinessTypes());
            bizService.saveName(bizName);

            String businessAddress = migrateBusinessRegistration.getBusinessAddress();
            BizStoreEntity bizStore = bizService.findMatchingStore(
                    migrateBusinessRegistration.getBusinessAddress(),
                    migrateBusinessRegistration.getBusinessPhoneNotFormatted());

            if (bizStore == null) {
                bizStore = BizStoreEntity.newInstance();
                bizStore.setBizName(bizName);
                bizStore.setPhone(migrateBusinessRegistration.getBusinessPhone());
                bizStore.setAddress(migrateBusinessRegistration.getBusinessAddress());
                validateAddress(bizStore);
                bizService.saveStore(bizStore);
            }

            BusinessUserEntity businessUser = businessUserService.findBusinessUser(migrateBusinessRegistration.getRid());
            businessUser
                    .setBizName(bizName)
                    .setBusinessUserRegistrationStatus(BusinessUserRegistrationStatusEnum.C);

            businessUserService.save(businessUser);
            migrateBusinessRegistration.setBusinessUser(businessUser);
            return migrateBusinessRegistration;
        } catch (Exception e) {
            LOG.error("Error updating business user profile rid={} reason={}",
                    migrateBusinessRegistration.getRid(), e.getLocalizedMessage(), e);
            throw new MigrateToBusinessRegistrationException("Error updating profile", e);
        }
    }

    /**
     * Update user profile info.
     *
     * @param br
     */
    private void updateUserProfile(MigrateBusinessRegistration br) {
        UserProfileEntity userProfile = userProfilePreferenceService.findByReceiptUserId(br.getRid());

        userProfile.setAddress(br.getAddress());
        userProfile.setCountryShortName(br.getCountryShortName());
        userProfile.setPhone(br.getPhoneNotFormatted());
        userProfilePreferenceService.updateProfile(userProfile);

        if (!userProfile.getFirstName().equals(br.getFirstName()) && !userProfile.getLastName().equals(br.getLastName())) {
            accountService.updateName(br.getFirstName(), br.getLastName(), br.getRid());
        }
    }

    @SuppressWarnings ("unused")
    public void validateAddress(BizStoreEntity bizStore) {
        if (null == bizStore.getId() || !bizStore.isValidatedUsingExternalAPI()) {
            externalService.decodeAddress(bizStore);
        }
    }

    @SuppressWarnings ("unused")
    public void updateProfile(MigrateBusinessRegistration migrateBusinessRegistration) {
        DecodedAddress decodedAddress = DecodedAddress.newInstance(externalService.getGeocodingResults(migrateBusinessRegistration.getAddress()), migrateBusinessRegistration.getAddress());
        if (decodedAddress.isNotEmpty()) {
            migrateBusinessRegistration.setAddress(decodedAddress.getFormattedAddress());
            migrateBusinessRegistration.setCountryShortName(decodedAddress.getCountryShortName());
        }
        migrateBusinessRegistration.setPhone(CommonUtil.phoneCleanup(migrateBusinessRegistration.getPhone()));
    }

    @SuppressWarnings ("unused")
    public void updateBusiness(MigrateBusinessRegistration migrateBusinessRegistration) {
        DecodedAddress decodedAddress = DecodedAddress.newInstance(externalService.getGeocodingResults(migrateBusinessRegistration.getBusinessAddress()), migrateBusinessRegistration.getAddress());
        if (decodedAddress.isNotEmpty()) {
            migrateBusinessRegistration.setBusinessAddress(decodedAddress.getFormattedAddress());
            migrateBusinessRegistration.setBusinessCountryShortName(decodedAddress.getCountryShortName());
        }
        migrateBusinessRegistration.setBusinessPhone(CommonUtil.phoneCleanup(migrateBusinessRegistration.getBusinessPhone()));
    }

    /**
     * Validate business user profile.
     *
     * @param migrateBusinessRegistration
     * @param messageContext
     * @return
     */
    @SuppressWarnings ("unused")
    public String validateUserProfileDetails(MigrateBusinessRegistration migrateBusinessRegistration, MessageContext messageContext) {
        LOG.info("Validate business user rid={}", migrateBusinessRegistration.getRid());
        String status = LandingController.SUCCESS;

        if (StringUtils.isBlank(migrateBusinessRegistration.getFirstName())) {
            messageContext.addMessage(
                    new MessageBuilder()
                            .error()
                            .source("firstName")
                            .defaultText("First name cannot be empty")
                            .build());
            status = "failure";
        }

        if (StringUtils.isBlank(migrateBusinessRegistration.getLastName())) {
            messageContext.addMessage(
                    new MessageBuilder()
                            .error()
                            .source("lastName")
                            .defaultText("Last name cannot be empty")
                            .build());
            status = "failure";
        }

        if (StringUtils.isBlank(migrateBusinessRegistration.getAddress())) {
            messageContext.addMessage(
                    new MessageBuilder()
                            .error()
                            .source("address")
                            .defaultText("Your Address cannot be Empty")
                            .build());
            status = "failure";
        }

        if (StringUtils.isBlank(migrateBusinessRegistration.getPhoneNotFormatted())) {
            messageContext.addMessage(
                    new MessageBuilder()
                            .error()
                            .source("phone")
                            .defaultText("Your Phone cannot be Empty")
                            .build());
            status = "failure";
        }

        LOG.info("Validate business user rid={} status={}", migrateBusinessRegistration.getRid(), status);
        return status;
    }

    /**
     * Validate business user profile.
     *
     * @param migrateBusinessRegistration
     * @param messageContext
     * @return
     */
    @SuppressWarnings ("unused")
    public String validateBusinessDetails(MigrateBusinessRegistration migrateBusinessRegistration, MessageContext messageContext) {
        LOG.info("Validate business rid={}", migrateBusinessRegistration.getRid());
        String status = "success";

        if (StringUtils.isBlank(migrateBusinessRegistration.getBusinessName())) {
            messageContext.addMessage(
                    new MessageBuilder()
                            .error()
                            .source("businessName")
                            .defaultText("Business Name cannot be empty")
                            .build());
            status = "failure";
        }

        if (null == migrateBusinessRegistration.getBusinessTypes()) {
            messageContext.addMessage(
                    new MessageBuilder()
                            .error()
                            .source("businessTypes")
                            .defaultText("Business Type is not selected")
                            .build());
            status = "failure";
        }

        if (StringUtils.isBlank(migrateBusinessRegistration.getBusinessAddress())) {
            messageContext.addMessage(
                    new MessageBuilder()
                            .error()
                            .source("businessAddress")
                            .defaultText("Business Address cannot be empty")
                            .build());
            status = "failure";
        }

        if (StringUtils.isBlank(migrateBusinessRegistration.getBusinessPhoneNotFormatted())) {
            messageContext.addMessage(
                    new MessageBuilder()
                            .error()
                            .source("businessPhone")
                            .defaultText("Business Phone cannot be Empty")
                            .build());
            status = "failure";
        }

        LOG.info("Validate business rid={} status={}", migrateBusinessRegistration.getRid(), status);
        return status;
    }
}
