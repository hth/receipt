/**
 *
 */
package com.receiptofi.repository;

import org.springframework.data.mongodb.core.mapping.Document;

import com.receiptofi.domain.BaseEntity;
import com.receiptofi.domain.UserPreferenceEntity;
import com.receiptofi.domain.UserProfileEntity;

/**
 * @author hitender
 * @since Dec 24, 2012 3:19:07 PM
 *
 */
public interface UserPreferenceManager extends RepositoryManager<UserPreferenceEntity> {
	static String TABLE = BaseEntity.getClassAnnotationValue(UserPreferenceEntity.class, Document.class, "collection");

	UserPreferenceEntity getObjectUsingUserProfile(UserProfileEntity userProfile);
}
