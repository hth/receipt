/**
 *
 */
package com.tholix.repository;

import java.util.List;

import org.springframework.data.mongodb.core.mapping.Document;

import com.mongodb.WriteResult;

import com.tholix.domain.BaseEntity;
import com.tholix.domain.UserAuthenticationEntity;
import com.tholix.domain.UserProfileEntity;
import com.tholix.domain.types.UserLevelEnum;

/**
 * @author hitender
 * @since Dec 23, 2012 3:45:26 AM
 *
 */
public interface UserProfileManager extends RepositoryManager<UserProfileEntity> {
	static String TABLE = BaseEntity.getClassAnnotationValue(UserProfileEntity.class, Document.class, "collection");

	UserProfileEntity getObjectUsingUserAuthentication(UserAuthenticationEntity object);

	UserProfileEntity getObjectUsingEmail(String emailId);

	/**
	 * Used for searching user based on name. Search could be based on First Name or Last Name.
	 * The list is sorted based on First Name. Displayed with format First Name, Last Name.
	 * @param name
	 * @return
	 */
	List<UserProfileEntity> searchAllByName(String name);

    UserProfileEntity findOneByEmail(String emailId);

	WriteResult updateObject(String id, UserLevelEnum level);
}
