/**
 *
 */
package com.tholix.repository;

import org.springframework.data.mongodb.core.mapping.Document;

import com.tholix.domain.BaseEntity;
import com.tholix.domain.ItemFeatureEntity;

/**
 * @author hitender
 * @when Dec 26, 2012 9:20:40 PM
 *
 */
public interface ItemFeatureManager extends RepositoryManager<ItemFeatureEntity> {
	public static String TABLE = BaseEntity.getClassAnnotationValue(ItemFeatureEntity.class, Document.class, "collection");
}
