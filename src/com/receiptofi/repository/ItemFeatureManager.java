/**
 *
 */
package com.receiptofi.repository;

import org.springframework.data.mongodb.core.mapping.Document;

import com.receiptofi.domain.BaseEntity;
import com.receiptofi.domain.ItemFeatureEntity;

/**
 * @author hitender
 * @since Dec 26, 2012 9:20:40 PM
 *
 */
public interface ItemFeatureManager extends RepositoryManager<ItemFeatureEntity> {
	public static String TABLE = BaseEntity.getClassAnnotationValue(ItemFeatureEntity.class, Document.class, "collection");
}
