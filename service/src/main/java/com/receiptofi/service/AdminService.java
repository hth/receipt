package com.receiptofi.service;

import com.receiptofi.domain.UserAccountEntity;
import com.receiptofi.domain.UserProfileEntity;
import com.receiptofi.domain.types.UserLevelEnum;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

/**
 * User: hitender
 * Date: 5/16/16 7:28 PM
 */
@Service
public class AdminService {
    private static final Logger LOG = LoggerFactory.getLogger(AdminService.class);

    private UserProfilePreferenceService userProfilePreferenceService;
    private BusinessUserService businessUserService;
    private AccountService accountService;

    @Autowired
    public AdminService(
            UserProfilePreferenceService userProfilePreferenceService,
            BusinessUserService businessUserService,
            AccountService accountService
    ) {
        this.userProfilePreferenceService = userProfilePreferenceService;
        this.businessUserService = businessUserService;
        this.accountService = accountService;
    }

    @PreAuthorize ("hasAnyRole('ROLE_ADMIN')")
    public boolean changeUserLevel(
            String adminRid,
            String rid,
            UserLevelEnum userLevel,
            boolean active
    ) {
        UserProfileEntity userProfile = userProfilePreferenceService.forProfilePreferenceFindByReceiptUserId(rid);

        userProfile.setLevel(userLevel);
        UserAccountEntity userAccount = accountService.changeAccountRolesToMatchUserLevel(
                userProfile.getReceiptUserId(),
                userProfile.getLevel()
        );

        /** Profile active and inactive updates UserProfileEntity and UserAccountEntity as active or inactive. */
        if (active) {
            userProfile.active();
            userAccount.active();
        } else {
            userProfile.inActive();
            userAccount.inActive();
        }

        try {
            accountService.saveUserAccount(userAccount);
            userProfilePreferenceService.updateProfile(userProfile);
            businessUserService.saveUpdateBusinessUser(rid, userProfile.getLevel(), active);
        } catch (Exception exce) {
            //XXX todo should there be two phase commit
            LOG.error("Failed updating User Profile, rid={} by adminRid={}", rid, adminRid, exce);
            return false;
        }

        return true;
    }
}
