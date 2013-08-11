package com.tholix.service;

import org.apache.log4j.Logger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.tholix.domain.BrowserEntity;
import com.tholix.domain.UserAuthenticationEntity;
import com.tholix.domain.UserProfileEntity;
import com.tholix.repository.BrowserManager;
import com.tholix.repository.UserAuthenticationManager;

/**
 * User: hitender
 * Date: 4/27/13
 * Time: 9:33 PM
 */
@Service
public final class LoginService {
    private static Logger log = Logger.getLogger(LoginService.class);

    @Autowired private UserAuthenticationManager userAuthenticationManager;
    @Autowired private BrowserManager browserManager;

    public UserAuthenticationEntity loadAuthenticationEntity(UserProfileEntity userProfileEntity) {
        return userAuthenticationManager.findOne(userProfileEntity.getUserAuthentication().getId());
    }

    public void saveUpdateBrowserInfo(String cookieId, String ip, String userAgent) {
        try {
            BrowserEntity browserEntity = browserManager.findOne(cookieId);
            if(browserEntity == null) {
                browserEntity = BrowserEntity.newInstance(cookieId, ip, userAgent);
                browserManager.save(browserEntity);
            } else {
                browserEntity.setUpdated();
                browserManager.save(browserEntity);
            }
        } catch(Exception e) {
            log.error("Moving on. Omitting this error message: " + e.getLocalizedMessage());
        }
    }
}
