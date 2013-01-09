/**
 * 
 */
package com.tholix.service;

import org.springframework.data.mongodb.core.mapping.Document;

import com.tholix.domain.BaseEntity;
import com.tholix.domain.UserAuthenticationEntity;
import com.tholix.domain.UserProfileEntity;

/**
 * @author hitender
 * @when Dec 23, 2012 3:45:26 AM
 * 
 */
public interface UserProfileManager extends RepositoryManager<UserProfileEntity> {
	public static String TABLE = BaseEntity.getClassAnnotationValue(UserProfileEntity.class, Document.class, "collection");

	public UserProfileEntity getObject(UserAuthenticationEntity object);

	public UserProfileEntity getObjectUsingEmail(String emailId);
}
