/**
 *
 */
package com.receiptofi.repository;

import com.mongodb.WriteResult;

import com.receiptofi.domain.DocumentEntity;
import com.receiptofi.domain.ItemEntityOCR;

import java.util.List;

/**
 * @author hitender
 * @since Jan 6, 2013 1:35:23 PM
 */
public interface ItemOCRManager extends RepositoryManager<ItemEntityOCR> {

    void saveObjects(List<ItemEntityOCR> objects) throws Exception;

    WriteResult updateObject(ItemEntityOCR object);

    List<ItemEntityOCR> getWhereReceipt(DocumentEntity receipt);

    void deleteWhere(String did);

    /**
     * Collection size.
     */
    long collectionSize();
}
