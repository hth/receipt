/**
 *
 */
package com.receiptofi.repository;

import com.receiptofi.domain.UserPreferenceEntity;
import com.receiptofi.domain.UserProfileEntity;

import java.util.List;

/**
 * @author hitender
 * @since Dec 24, 2012 3:19:07 PM
 */
public interface UserPreferenceManager extends RepositoryManager<UserPreferenceEntity> {

    UserPreferenceEntity getById(String id);

    UserPreferenceEntity getByRid(String rid);

    UserPreferenceEntity getObjectUsingUserProfile(UserProfileEntity userProfile);

    List<UserPreferenceEntity> getAll();
}
