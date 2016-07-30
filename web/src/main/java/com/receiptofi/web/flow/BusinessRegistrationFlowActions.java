package com.receiptofi.web.flow;

import com.receiptofi.domain.BizNameEntity;
import com.receiptofi.domain.BizStoreEntity;
import com.receiptofi.domain.BusinessUserEntity;
import com.receiptofi.domain.UserProfileEntity;
import com.receiptofi.domain.flow.BusinessRegistration;
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
import com.receiptofi.web.flow.exception.BusinessRegistrationException;

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
 * User: hitender
 * Date: 5/20/16 9:51 PM
 */
@Component
public class BusinessRegistrationFlowActions {
    private static final Logger LOG = LoggerFactory.getLogger(BusinessRegistrationFlowActions.class);

    private FetcherService fetcherService;
    private UserProfilePreferenceService userProfilePreferenceService;
    private AccountService accountService;
    private BusinessUserService businessUserService;
    private BizService bizService;
    private ExternalService externalService;

    @SuppressWarnings("all")
    @Autowired
    public BusinessRegistrationFlowActions(
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
    public BusinessRegistration createBusinessRegistration() {
        ReceiptUser receiptUser = (ReceiptUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String rid = receiptUser.getRid();

        BusinessUserEntity businessUser = businessUserService.findBusinessUser(rid);
        BizStoreEntity bizStore = null;
        if (businessUser.getBizName() != null) {
            bizStore = bizService.findOneBizStore(businessUser.getBizName().getId());
        }
        UserProfileEntity userProfile = userProfilePreferenceService.findByReceiptUserId(rid);
        return BusinessRegistration.newInstance(businessUser, bizStore)
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
     * @param businessRegistration
     * @return
     * @throws BusinessRegistrationException
     */
    @SuppressWarnings ("unused")
    public BusinessRegistration completeRegistrationInformation(BusinessRegistration businessRegistration)
            throws BusinessRegistrationException {
        try {
            updateUserProfile(businessRegistration);

            BizNameEntity bizName = bizService.findMatchingBusiness(businessRegistration.getBusinessName());
            if (null == bizName) {
                bizName = BizNameEntity.newInstance();
                bizName.setBusinessName(businessRegistration.getBusinessName());
            }
            bizName.setBusinessTypes(businessRegistration.getBusinessTypes());
            bizService.saveName(bizName);

            String businessAddress = businessRegistration.getBusinessAddress();
            BizStoreEntity bizStore = bizService.findMatchingStore(
                    businessRegistration.getBusinessAddress(),
                    businessRegistration.getBusinessPhoneNotFormatted());

            if (bizStore == null) {
                bizStore = BizStoreEntity.newInstance();
                bizStore.setBizName(bizName);
                bizStore.setPhone(businessRegistration.getBusinessPhone());
                bizStore.setAddress(businessRegistration.getBusinessAddress());
                validateAddress(bizStore);
                bizService.saveStore(bizStore);
            }

            BusinessUserEntity businessUser = businessUserService.findBusinessUser(businessRegistration.getRid());
            businessUser
                    .setBizName(bizName)
                    .setBusinessUserRegistrationStatus(BusinessUserRegistrationStatusEnum.C);

            businessUserService.save(businessUser);
            businessRegistration.setBusinessUser(businessUser);
            return businessRegistration;
        } catch (Exception e) {
            LOG.error("Error updating business user profile rid={} reason={}",
                    businessRegistration.getRid(), e.getLocalizedMessage(), e);
            throw new BusinessRegistrationException("Error updating profile", e);
        }
    }

    /**
     * Update user profile info.
     *
     * @param br
     */
    private void updateUserProfile(BusinessRegistration br) {
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

        if (StringUtils.isBlank(businessRegistration.getLastName())) {
            messageContext.addMessage(
                    new MessageBuilder()
                            .error()
                            .source("lastName")
                            .defaultText("Last name cannot be empty")
                            .build());
            status = "failure";
        }

        if (StringUtils.isBlank(businessRegistration.getAddress())) {
            messageContext.addMessage(
                    new MessageBuilder()
                            .error()
                            .source("address")
                            .defaultText("Your Address cannot be Empty")
                            .build());
            status = "failure";
        }

        if (StringUtils.isBlank(businessRegistration.getPhoneNotFormatted())) {
            messageContext.addMessage(
                    new MessageBuilder()
                            .error()
                            .source("phone")
                            .defaultText("Your Phone cannot be Empty")
                            .build());
            status = "failure";
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
