/**
 *
 */
package com.receiptofi.repository;

import com.receiptofi.domain.BaseEntity;
import com.receiptofi.domain.ItemFeatureEntity;

import org.springframework.data.mongodb.core.mapping.Document;

/**
 * @author hitender
 * @since Dec 26, 2012 9:20:40 PM
 *
 */
public interface ItemFeatureManager extends RepositoryManager<ItemFeatureEntity> {
	static String TABLE = BaseEntity.getClassAnnotationValue(ItemFeatureEntity.class, Document.class, "collection");
}
