package com.receiptofi.service;

import com.receiptofi.domain.UserProfileEntity;
import com.receiptofi.repository.UserProfileManager;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * User: hitender
 * Date: 4/28/13
 * Time: 8:34 PM
 */
@SuppressWarnings ({
        "PMD.BeanMembersShouldSerialize",
        "PMD.LocalVariableCouldBeFinal",
        "PMD.MethodArgumentCouldBeFinal",
        "PMD.LongVariable"
})
@Service
public class AdminLandingService {
    private static final Logger LOG = LoggerFactory.getLogger(AdminLandingService.class);

    @Autowired private UserProfileManager userProfileManager;

    /**
     * This method is called from AJAX to get the matching list of users in the system
     *
     * @param name
     * @return
     */
    public List<String> findMatchingUsers(String name) {
        List<String> users = new ArrayList<>();
        List<UserProfileEntity> userProfileEntities = userProfileManager.searchAllByName(name);
        for (UserProfileEntity userProfile : userProfileEntities) {
            users.add(userProfile.getFirstName() + ", " + userProfile.getLastName());
        }
        LOG.debug("List of users={}", users);
        return users;
    }

    /**
     * This method returns well populated users with 'id' and other relevant data for showing user profile.
     *
     * @param name
     * @return
     */
    public List<UserProfileEntity> findAllUsers(String name) {
        LOG.info("Search string for user name={}", name);
        return userProfileManager.searchAllByName(name);
    }
}
