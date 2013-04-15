/**
 *
 */
package com.tholix.web;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

import org.springframework.beans.factory.annotation.Autowired;

import com.tholix.domain.UserAuthenticationEntity;
import com.tholix.domain.UserProfileEntity;
import com.tholix.service.UserAuthenticationManager;
import com.tholix.service.UserProfileManager;
import com.tholix.utils.ValidateObjectID;
import com.tholix.web.rest.Header;

/**
 * @author hitender
 * @when Mar 28, 2013 2:00:46 PM
 *
 */
public abstract class BaseController {
	private static final Logger log = Logger.getLogger(BaseController.class);

    @Autowired UserAuthenticationManager userAuthenticationManager;
    @Autowired UserProfileManager userProfileManager;

    public String getAuth(String profileId) {
        log.debug("Find user with profileId: " + profileId);
        return getAuth(userProfileManager.findOne(profileId));
    }

    public String getAuth(UserProfileEntity userProfile) {
        UserAuthenticationEntity userAuthentication = userAuthenticationManager.findOne(userProfile.getUserAuthentication().getId());
        return userAuthentication.getAuth();
    }

    public UserProfileEntity authenticate(String profileId, String authKey) {
        if(isValid(profileId, authKey)) {
            UserProfileEntity userProfile = userProfileManager.findOne(profileId);
            if(checkAuthKey(authKey, userProfile)) {
                return userProfile;
            }
            return null;
        }
        return null;
    }

    public boolean isAuthenticate(String profileId, String authKey) {
        if(isValid(profileId, authKey)) {
            UserProfileEntity userProfile = userProfileManager.findOne(profileId);
            if(checkAuthKey(authKey, userProfile)) {
                return true;
            }
            return false;
        }
        return false;
    }

    /**
     *
     * @param authKey
     * @param userProfile
     * @return
     */
    private boolean checkAuthKey(String authKey, UserProfileEntity userProfile) {
        return userProfile != null && authKey.equals(userProfile.getUserAuthentication().getAuth());
    }

    /**
     * Validates if the Profile Id and Auth Key is not empty and valid as Object ID
     * @param profileId
     * @param authKey
     * @return
     */
    private boolean isValid(String profileId, String authKey) {
        return StringUtils.isNotEmpty(profileId) && ValidateObjectID.isValid(profileId) && StringUtils.isNotEmpty(authKey);
    }

    /**
     * Header for failure
     * @return
     */
    public Header getHeaderForProfileOrAuthFailure() {
        Header header = Header.newInstanceFailure();
        header.setStatus(Header.RESULT.AUTH_FAILURE);
        header.setMessage("Profile or Authorization key missing or invalid");
        return header;
    }
}
