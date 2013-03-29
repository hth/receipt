/**
 * 
 */
package com.tholix.service;

import java.util.List;

import org.springframework.data.mongodb.core.mapping.Document;

import com.mongodb.WriteResult;
import com.tholix.domain.BaseEntity;
import com.tholix.domain.UserAuthenticationEntity;
import com.tholix.domain.UserProfileEntity;
import com.tholix.domain.types.UserLevelEnum;

/**
 * @author hitender
 * @when Dec 23, 2012 3:45:26 AM
 * 
 */
public interface UserProfileManager extends RepositoryManager<UserProfileEntity> {
	public static String TABLE = BaseEntity.getClassAnnotationValue(UserProfileEntity.class, Document.class, "collection");

	public UserProfileEntity getObjectUsingUserAuthentication(UserAuthenticationEntity object);

	public UserProfileEntity getObjectUsingEmail(String emailId);
	
	/**
	 * Used for searching user based on name. Search could be based on First Name or Last Name. 
	 * The list is sorted based on Last Name. Displayed with format Last Name, First Name.
	 * @param name
	 * @return
	 */
	public List<UserProfileEntity> searchUser(String name);
	
	public WriteResult updateObject(String id, UserLevelEnum level);
}
