package com.receiptofi.service;

import com.receiptofi.domain.InviteEntity;
import com.receiptofi.domain.UserProfileEntity;
import com.receiptofi.domain.types.AccountTypeEnum;
import com.receiptofi.repository.InviteManager;
import com.receiptofi.repository.UserProfileManager;
import com.receiptofi.utils.HashText;
import com.receiptofi.utils.RandomString;
import com.receiptofi.web.form.UserRegistrationForm;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * User: hitender
 * Date: 6/9/13
 * Time: 2:06 PM
 */
@Service
public final class InviteService {
    private static Logger log = LoggerFactory.getLogger(InviteService.class);

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

            String authenticationKey = HashText.bCrypt(RandomString.newInstance().nextString());
            inviteEntity = InviteEntity.newInstance(emailId, authenticationKey, newInvitedUser, userProfile);
            inviteManager.save(inviteEntity);
            return inviteEntity;
        } catch (Exception exception) {
            log.error("Error occurred during creation of invited user: " + exception.getLocalizedMessage());
            throw exception;
        }
    }

    /**
     * Re-Invite only when the invite is active
     *
     * @param emailId
     * @return
     */
    public InviteEntity reInviteActiveInvite(String emailId, UserProfileEntity userProfile) {
        return inviteManager.reInviteActiveInvite(emailId, userProfile);
    }

    public InviteEntity find(String emailId) {
        return inviteManager.find(emailId);
    }

    public InviteEntity findInviteAuthenticationForKey(String key) {
        return inviteManager.findByAuthenticationKey(key);
    }

    public void invalidateAllEntries(InviteEntity inviteEntity) {
        inviteManager.invalidateAllEntries(inviteEntity);
    }
}
