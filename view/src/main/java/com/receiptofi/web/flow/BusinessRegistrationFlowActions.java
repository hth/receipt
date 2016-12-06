package com.receiptofi.web.flow;

import com.receiptofi.domain.BizNameEntity;
import com.receiptofi.domain.BusinessUserEntity;
import com.receiptofi.domain.InviteEntity;
import com.receiptofi.domain.flow.BusinessRegistration;
import com.receiptofi.domain.flow.Register;
import com.receiptofi.domain.types.BusinessUserRegistrationStatusEnum;
import com.receiptofi.service.BizService;
import com.receiptofi.service.BusinessUserService;
import com.receiptofi.service.ExternalService;
import com.receiptofi.service.InviteService;
import com.receiptofi.web.controller.open.LoginController;
import com.receiptofi.web.flow.exception.BusinessRegistrationException;

import org.apache.commons.lang3.StringUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * User: hitender
 * Date: 7/27/16 4:04 PM
 */
@Component
public class BusinessRegistrationFlowActions extends RegistrationFlowActions {
    private static final Logger LOG = LoggerFactory.getLogger(BusinessRegistrationFlowActions.class);

    private InviteService inviteService;
    private BusinessUserService businessUserService;
    private LoginController loginController;

    @Value ("${registration.turned.on}")
    private boolean registrationTurnedOn;

    @SuppressWarnings ("all")
    @Autowired
    public BusinessRegistrationFlowActions(
            InviteService inviteService,
            ExternalService externalService,
            BusinessUserService businessUserService,
            BizService bizService,
            LoginController loginController) {
        super(externalService, bizService);
        this.inviteService = inviteService;
        this.businessUserService = businessUserService;
        this.loginController = loginController;
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
    public String completeRegistrationInformation(Register register, String key)
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

            BizNameEntity bizName = registerBusinessDetails(register);
            BusinessUserEntity businessUser = businessUserService.findBusinessUser(register.getRegisterUser().getRid());
            if (null == businessUser) {
                businessUser = BusinessUserEntity.newInstance(register.getRegisterUser().getRid(), invite.getUserLevel());
            }
            businessUser
                    .setBizName(bizName)
                    .setBusinessUserRegistrationStatus(BusinessUserRegistrationStatusEnum.C);

            businessUserService.save(businessUser);
            register.getRegisterBusiness().setBusinessUser(businessUser);
            String redirectTo = loginController.continueLoginAfterRegistration(register.getRegisterUser().getRid());
            LOG.info("Redirecting user to {}", redirectTo);

            return redirectTo;
        } catch (Exception e) {
            LOG.error("Error updating business user profile rid={} reason={}",
                    register.getRegisterUser().getRid(), e.getLocalizedMessage(), e);
            throw new BusinessRegistrationException("Error updating profile", e);
        }
    }
}
