/**
 * 
 */
package com.tholix.service;

import org.springframework.data.mongodb.core.mapping.Document;

import com.tholix.domain.BaseEntity;
import com.tholix.domain.UserAuthenticationEntity;

/**
 * @author hitender
 * @when Dec 16, 2012 1:20:31 PM
 */
public interface UserAuthenticationManager extends RepositoryManager<UserAuthenticationEntity> {
	public static String TABLE = BaseEntity.getClassAnnotationValue(UserAuthenticationEntity.class, Document.class, "collection");

}
