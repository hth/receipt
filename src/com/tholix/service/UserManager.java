/**
 * 
 */
package com.tholix.service;

import org.springframework.data.mongodb.core.mapping.Document;

import com.tholix.domain.BaseEntity;
import com.tholix.domain.UserEntity;

/**
 * @author hitender
 * @when Dec 16, 2012 1:20:31 PM
 */
public interface UserManager extends RepositoryManager<UserEntity> {
	public static String TABLE = BaseEntity.getClassAnnotationValue(UserEntity.class, Document.class, "collection");

}
