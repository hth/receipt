/**
 * 
 */
package com.tholix.service;

import org.springframework.data.mongodb.core.mapping.Document;

import com.tholix.domain.BaseEntity;
import com.tholix.domain.UserPreferenceEntity;

/**
 * @author hitender
 * @when Dec 24, 2012 3:19:07 PM
 * 
 */
public interface UserPreferenceManager extends RepositoryManager<UserPreferenceEntity> {
	public static String TABLE = BaseEntity.getClassAnnotationValue(UserPreferenceEntity.class, Document.class, "collection");
}
