/**
 *
 */
package com.tholix.repository;

import org.springframework.data.mongodb.core.mapping.Document;

import com.tholix.domain.BaseEntity;
import com.tholix.domain.UserPreferenceEntity;
import com.tholix.domain.UserProfileEntity;

/**
 * @author hitender
 * @since Dec 24, 2012 3:19:07 PM
 *
 */
public interface UserPreferenceManager extends RepositoryManager<UserPreferenceEntity> {
	public static String TABLE = BaseEntity.getClassAnnotationValue(UserPreferenceEntity.class, Document.class, "collection");

	public UserPreferenceEntity getObjectUsingUserProfile(UserProfileEntity userProfile);
}
