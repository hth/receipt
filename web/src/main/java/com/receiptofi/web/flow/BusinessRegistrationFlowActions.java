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
}
