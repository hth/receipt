package com.receiptofi.service;

import com.receiptofi.domain.InviteEntity;
import com.receiptofi.domain.UserAccountEntity;
import com.receiptofi.domain.UserAuthenticationEntity;
import com.receiptofi.domain.UserProfileEntity;
import com.receiptofi.domain.types.UserLevelEnum;
import com.receiptofi.repository.InviteManager;
import com.receiptofi.repository.UserAccountManager;
import com.receiptofi.repository.UserProfileManager;
import com.receiptofi.utils.DateUtil;
import com.receiptofi.utils.HashText;
import com.receiptofi.utils.RandomString;

import org.apache.commons.lang3.StringUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

/**
 * User: hitender
 * Date: 6/9/13
 * Time: 2:06 PM
 */
@SuppressWarnings ({
        "PMD.BeanMembersShouldSerialize",
        "PMD.LocalVariableCouldBeFinal",
        "PMD.MethodArgumentCouldBeFinal",
        "PMD.LongVariable"
})
@Service
public class InviteService {
    private static final Logger LOG = LoggerFactory.getLogger(InviteService.class);

    private AccountService accountService;
    private InviteManager inviteManager;
    private UserProfileManager userProfileManager;
    private UserAccountManager userAccountManager;

    @Autowired
    public InviteService(
            AccountService accountService,
            InviteManager inviteManager,
            UserProfileManager userProfileManager,
            UserAccountManager userAccountManager
    ) {
        this.accountService = accountService;
        this.inviteManager = inviteManager;
        this.userProfileManager = userProfileManager;
        this.userAccountManager = userAccountManager;
    }

    /**
     * @param invitedUserEmail
     * @param invitedBy
     * @return
     */
    InviteEntity initiateInvite(String invitedUserEmail, UserAccountEntity invitedBy, UserLevelEnum userLevel) {
        UserAccountEntity userAccount = createNewUserAccount(invitedUserEmail);
        return createInvite(invitedUserEmail, invitedBy, userAccount, userLevel);
    }

    private InviteEntity createInvite(
            String invitedUserEmail,
            UserAccountEntity invitedBy,
            UserAccountEntity userAccount,
            UserLevelEnum userLevel
    ) {
        UserProfileEntity newInvitedUser = userProfileManager.findByReceiptUserId(userAccount.getReceiptUserId());
        newInvitedUser.inActive();
        userProfileManager.save(newInvitedUser);

        InviteEntity inviteEntity = InviteEntity.newInstance(
                invitedUserEmail,
                HashText.computeBCrypt(RandomString.newInstance().nextString()),
                newInvitedUser,
                invitedBy,
                userLevel
        );
        inviteManager.save(inviteEntity);
        return inviteEntity;
    }

    private UserAccountEntity createNewUserAccount(String invitedUserEmail) {
        UserAccountEntity userAccount;
        try {
            //First save is performed
            userAccount = accountService.createNewAccount(
                    invitedUserEmail,
                    "",
                    "",
                    "",
                    ""
            );
        } catch (RuntimeException e) {
            LOG.error("Error occurred during creation of invited user reason={}", e.getLocalizedMessage(), e);
            throw e;
        }

        /* Updating the record as inactive until user completes registration. */
        userAccount.inActive();
        userAccountManager.save(userAccount);
        return userAccount;
    }

    /**
     * Re-Invite only when the invite is active
     *
     * @param emailId
     * @return
     */
    InviteEntity reInviteActiveInvite(String emailId, UserAccountEntity invitedBy) {
        return inviteManager.reInviteActiveInvite(emailId, invitedBy);
    }

    public InviteEntity find(String emailId) {
        return inviteManager.find(emailId);
    }

    public InviteEntity findByAuthenticationKey(String key) {
        return inviteManager.findByAuthenticationKey(key);
    }

    private void invalidateAllEntries(InviteEntity invite) {
        inviteManager.invalidateAllEntries(invite);
    }

    public void save(InviteEntity invite) {
        inviteManager.save(invite);
    }

    public void deleteHard(InviteEntity invite) {
        inviteManager.deleteHard(invite);
    }

    public boolean completeProfileForInvitationSignup(
            String firstName,
            String lastName,
            String birthday,
            String address,
            String countryShortName,
            String phone,
            String password,
            InviteEntity invite
    ) {
        Assert.notNull(invite, "Invite is null");
        UserProfileEntity userProfile = invite.getInvited();
        userProfile.setFirstName(firstName);
        userProfile.setLastName(lastName);
        if (StringUtils.isNotBlank(birthday)) {
            userProfile.setBirthday(DateUtil.parseAgeForBirthday(birthday));
        }
        userProfile.setLevel(invite.getUserLevel());
        userProfile.active();
        userProfile.setAddress(address);
        userProfile.setCountryShortName(countryShortName);
        userProfile.setPhone(phone);

        /* Updates ROLE based on Invite UserLevel. */
        UserAccountEntity userAccount = accountService.changeAccountRolesToMatchUserLevel(
                userProfile.getReceiptUserId(),
                invite.getUserLevel());

        UserAuthenticationEntity userAuthentication = UserAuthenticationEntity.newInstance(
                HashText.computeBCrypt(password),
                HashText.computeBCrypt(RandomString.newInstance().nextString())
        );
        userAuthentication.setId(userAccount.getUserAuthentication().getId());
        userAuthentication.setVersion(userAccount.getUserAuthentication().getVersion());
        userAuthentication.setCreated(userAccount.getUserAuthentication().getCreated());
        try {
            userProfileManager.save(userProfile);
            accountService.updateAuthentication(userAuthentication);

            userAccount.setFirstName(userProfile.getFirstName());
            userAccount.setLastName(userProfile.getLastName());
            userAccount.active();
            userAccount.setAccountValidated(true);
            userAccount.setUserAuthentication(userAuthentication);
            accountService.saveUserAccount(userAccount);

            invalidateAllEntries(invite);
            return true;
        } catch (Exception e) {
            LOG.error("Error during updating of the old authentication keys={}", e.getLocalizedMessage(), e);
        }
        return false;
    }
}
