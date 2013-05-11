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
    public static String TABLE = BaseEntity.getClassAnnotationValue(BizStoreEntity.class, Document.class, "collection");

    BizStoreEntity noStore();

    BizStoreEntity findOne(BizStoreEntity bizStoreEntity);

    List<BizStoreEntity> findAll(String bizAddress, BizNameEntity bizNameEntity);

    /**
     * Used for Ajax
     *
     * @param bizAddress
     * @param bizNameEntity
     * @return
     */
    List<String> findAllAddress(String bizAddress, BizNameEntity bizNameEntity);

    /**
     * BizStore sorted on create date and limited to latest records
     *
     * @param bizNameEntity
     * @param limit
     * @return
     */
    List<BizStoreEntity> findAllAddress(BizNameEntity bizNameEntity, int limit);
}
