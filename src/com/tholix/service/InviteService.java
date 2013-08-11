package com.tholix.service;

import org.apache.log4j.Logger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.tholix.domain.InviteEntity;
import com.tholix.domain.UserProfileEntity;
import com.tholix.domain.types.AccountTypeEnum;
import com.tholix.repository.InviteManager;
import com.tholix.repository.UserProfileManager;
import com.tholix.utils.RandomString;
import com.tholix.utils.SHAHashing;
import com.tholix.web.form.UserRegistrationForm;

/**
 * User: hitender
 * Date: 6/9/13
 * Time: 2:06 PM
 */
@Service
public final class InviteService {
    private static Logger log = Logger.getLogger(InviteService.class);

    @Autowired private AccountService accountService;
    @Autowired private InviteManager inviteManager;
    @Autowired private UserProfileManager userProfileManager;

    /**
     *
     * @param emailId
     * @param userProfile
     * @return
     */
    public InviteEntity initiateInvite(String emailId, UserProfileEntity userProfile) throws Exception {
        UserRegistrationForm userRegistrationForm = UserRegistrationForm.newInstance();
        userRegistrationForm.setEmailId(emailId);
        userRegistrationForm.setAccountType(AccountTypeEnum.PERSONAL);
        userRegistrationForm.setFirstName("");
        userRegistrationForm.setLastName("");
        userRegistrationForm.setPassword(RandomString.newInstance(8).nextString());

        InviteEntity inviteEntity;
        try {
            //First save is performed
            UserProfileEntity newInvitedUser = accountService.createNewAccount(userRegistrationForm);

            //Updating the record as inactive until user completes registration
            newInvitedUser.inActive();
            userProfileManager.save(newInvitedUser);

            String authenticationKey = SHAHashing.hashCodeSHA512(RandomString.newInstance().nextString());
            inviteEntity = InviteEntity.newInstance(emailId, authenticationKey, newInvitedUser, userProfile);
            inviteManager.save(inviteEntity);
            return inviteEntity;
        } catch (Exception exception) {
            log.error("Error occurred during creation of invited user: " + exception.getLocalizedMessage());
            throw exception;
        }
    }

    public InviteEntity findInviteAuthenticationForKey(String key) {
        return inviteManager.findByAuthenticationKey(key);
    }

    public void invalidateAllEntries(InviteEntity inviteEntity) {
        inviteManager.invalidateAllEntries(inviteEntity);
    }
}
