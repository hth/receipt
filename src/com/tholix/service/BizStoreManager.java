package com.tholix.service;

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
}
