package com.tholix.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.tholix.domain.UserAuthenticationEntity;
import com.tholix.domain.UserProfileEntity;
import com.tholix.repository.UserAuthenticationManager;

/**
 * User: hitender
 * Date: 4/27/13
 * Time: 9:33 PM
 */
@Service
public class LoginService {

    @Autowired private UserAuthenticationManager userAuthenticationManager;

    public UserAuthenticationEntity loadAuthenticationEntity(UserProfileEntity userProfileEntity) {
        return userAuthenticationManager.findOne(userProfileEntity.getUserAuthentication().getId());
    }
}
