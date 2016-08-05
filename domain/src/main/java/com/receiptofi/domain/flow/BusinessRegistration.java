package com.receiptofi.domain.flow;

import com.receiptofi.domain.InviteEntity;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;

/**
 * User: hitender
 * Date: 7/27/16 5:29 PM
 */
public class BusinessRegistration extends Register implements Serializable {
    private static final Logger LOG = LoggerFactory.getLogger(BusinessRegistration.class);
    private static final long serialVersionUID = -6047892968409443583L;

    private BusinessRegistration(InviteEntity invite, boolean registrationTurnedOn) {
        getRegisterUser().setRid(invite.getInvited().getReceiptUserId());
        getRegisterUser().setEmail(invite.getInvited().getEmail());
        getRegisterUser().setEmailValidated(true);
        getRegisterUser().setRegistrationTurnedOn(registrationTurnedOn);
        getRegisterUser().setAccountExists(false);
    }

    public static BusinessRegistration newInstance(InviteEntity invite, boolean registrationTurnedOn) {
        return new BusinessRegistration(invite, registrationTurnedOn);
    }
}
