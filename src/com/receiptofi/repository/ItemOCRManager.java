/**
 *
 */
package com.receiptofi.repository;

import com.receiptofi.domain.BaseEntity;
import com.receiptofi.domain.DocumentEntity;
import com.receiptofi.domain.ItemEntityOCR;

import java.util.List;

import org.springframework.data.mongodb.core.mapping.Document;

import com.mongodb.WriteResult;

/**
 * @author hitender
 * @since Jan 6, 2013 1:35:23 PM
 *
 */
public interface ItemOCRManager extends RepositoryManager<ItemEntityOCR> {

	public static String TABLE = BaseEntity.getClassAnnotationValue(ItemEntityOCR.class, Document.class, "collection");

	public void saveObjects(List<ItemEntityOCR> objects) throws Exception;

	public WriteResult updateObject(ItemEntityOCR object);

	public List<ItemEntityOCR> getWhereReceipt(DocumentEntity receipt);

	public void deleteWhereReceipt(DocumentEntity receipt);
}
