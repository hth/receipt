package com.receiptofi.repository;

import com.receiptofi.domain.BaseEntity;
import com.receiptofi.domain.BizNameEntity;

import java.util.List;
import java.util.Set;

import org.springframework.data.mongodb.core.mapping.Document;

/**
 * User: hitender
 * Date: 4/22/13
 * Time: 11:08 PM
 */
public interface BizNameManager extends RepositoryManager<BizNameEntity> {
    static String TABLE = BaseEntity.getClassAnnotationValue(BizNameEntity.class, Document.class, "collection");

    BizNameEntity noName();

    /**
     * Find one Biz Name for the supplied value for the column businessName
     *
     * @param businessName
     * @return
     */
    BizNameEntity findOneByName(String businessName);

    /**
     * Find all the Business with businessName
     *
     * @param businessName
     * @return
     */
    List<BizNameEntity> findAllBizWithMatchingName(String businessName);

    /**
     * Find all the Business with businessName. Mostly used for Ajax call listing.
     *
     * @param businessName
     * @return
     */
    Set<String> findAllDistinctBizStr(String businessName);
}
