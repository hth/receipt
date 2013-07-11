package com.tholix.repository;

import java.util.List;

import org.springframework.data.mongodb.core.mapping.Document;

import com.tholix.domain.BaseEntity;
import com.tholix.domain.BizNameEntity;
import com.tholix.domain.BizStoreEntity;

/**
 * User: hitender
 * Date: 4/22/13
 * Time: 11:20 PM
 */
public interface BizStoreManager extends RepositoryManager<BizStoreEntity> {
    static String TABLE = BaseEntity.getClassAnnotationValue(BizStoreEntity.class, Document.class, "collection");

    //TODO use annotation instead
    /** Field name */
    String ADDRESS = "ADDRESS";
    String PHONE = "PHONE";

    BizStoreEntity noStore();

    BizStoreEntity findOne(BizStoreEntity bizStoreEntity);

    List<BizStoreEntity> findAll(String bizAddress, BizNameEntity bizNameEntity);

    /**
     * Used for Ajax. Populates BizStoreEntity with just fieldName.
     *
     * @param bizAddress
     * @param bizNameEntity
     * @return
     */
    List<BizStoreEntity> getAllWithJustSpecificField(String bizAddress, BizNameEntity bizNameEntity, String fieldName);

    /**
     * Used for Ajax. Populates BizStoreEntity with just fieldName.
     *
     * @param bizPhone
     * @param bizAddress
     * @param bizNameEntity
     * @return
     */
    List<BizStoreEntity> getAllWithJustSpecificField(String bizPhone, String bizAddress, BizNameEntity bizNameEntity, String fieldName);

    /**
     * BizStore sorted on create date and limited to latest records
     *
     * @param bizNameEntity
     * @param limit
     * @return
     */
    List<BizStoreEntity> findAllAddress(BizNameEntity bizNameEntity, int limit);
}
