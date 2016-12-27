/**
 *
 */
package com.receiptofi.repository;

import com.receiptofi.domain.UserAuthenticationEntity;
import com.receiptofi.domain.UserProfileEntity;
import com.receiptofi.domain.annotation.Mobile;

import java.util.Date;
import java.util.List;

/**
 * @author hitender
 * @since Dec 23, 2012 3:45:26 AM
 */
public interface UserProfileManager extends RepositoryManager<UserProfileEntity> {
    UserProfileEntity getById(String id);

    UserProfileEntity getObjectUsingUserAuthentication(UserAuthenticationEntity object);

    UserProfileEntity findByEmail(String email);

    UserProfileEntity findByReceiptUserId(String rid);

    UserProfileEntity forProfilePreferenceFindByReceiptUserId(String rid);

    UserProfileEntity findByProviderUserId(String puid);

    UserProfileEntity findByProviderUserIdOrEmail(String puid, String email);

    /**
     * Used for searching user based on name. Search could be based on First Name or Last Name.
     * The list is sorted based on First Name. Displayed with format First Name, Last Name.
     *
     * @param name
     * @return
     */
    List<UserProfileEntity> searchAllByName(String name);

    UserProfileEntity findOneByMail(String emailId);

    @Mobile
    UserProfileEntity getProfileUpdateSince(String rid, Date since);

    @Mobile
    void updateCountryShortName(String country, String rid);

    List<UserProfileEntity> getAll();
}
