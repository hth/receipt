/**
 *
 */
package com.receiptofi.repository;

import org.springframework.data.mongodb.core.mapping.Document;

import com.receiptofi.domain.BaseEntity;
import com.receiptofi.domain.UserAuthenticationEntity;

/**
 * @author hitender
 * @since Dec 16, 2012 1:20:31 PM
 */
public interface UserAuthenticationManager extends RepositoryManager<UserAuthenticationEntity> {
	public static String TABLE = BaseEntity.getClassAnnotationValue(UserAuthenticationEntity.class, Document.class, "collection");

}
