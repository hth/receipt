/**
 *
 */
package com.receiptofi.web.controller;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

import org.springframework.beans.factory.annotation.Autowired;

import com.receiptofi.domain.UserAuthenticationEntity;
import com.receiptofi.domain.UserProfileEntity;
import com.receiptofi.repository.UserAuthenticationManager;
import com.receiptofi.repository.UserProfileManager;
import com.receiptofi.utils.ValidateObjectID;
import com.receiptofi.web.rest.Header;

/**
 * @author hitender
 * @since Mar 28, 2013 2:00:46 PM
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
        return userAuthentication.getAuthenticationKey();
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
        return userProfile != null && authKey.equals(userProfile.getUserAuthentication().getAuthenticationKey());
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
