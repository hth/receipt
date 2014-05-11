/**
 *
 */
package com.receiptofi.repository;

import com.receiptofi.domain.BaseEntity;
import com.receiptofi.domain.UserAuthenticationEntity;

import org.springframework.data.mongodb.core.mapping.Document;

/**
 * @author hitender
 * @since Dec 16, 2012 1:20:31 PM
 */
public interface UserAuthenticationManager extends RepositoryManager<UserAuthenticationEntity> {
	String TABLE = BaseEntity.getClassAnnotationValue(UserAuthenticationEntity.class, Document.class, "collection");
}
