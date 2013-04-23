package com.tholix.service;

import java.util.List;

import org.springframework.data.mongodb.core.mapping.Document;

import com.tholix.domain.BaseEntity;
import com.tholix.domain.BizNameEntity;
import com.tholix.domain.BusinessEntity;

/**
 * User: hitender
 * Date: 4/22/13
 * Time: 11:08 PM
 */
public interface BizNameManager extends RepositoryManager<BizNameEntity> {
    public static String TABLE = BaseEntity.getClassAnnotationValue(BizNameEntity.class, Document.class, "collection");

    BizNameEntity noName();

    /**
     * Find one Biz Name for the supplied value for the column name
     *
     * @param columnName
     * @param value
     * @return
     */
    BizNameEntity findOne(String columnName, String value);

    /**
     * Find all the Business with name
     *
     * @param bizName
     * @return
     */
    List<BizNameEntity> findAllBiz(String bizName);

    /**
     * Find all the Business with name. Mostly used for Ajax call listing.
     *
     * @param bizName
     * @return
     */
    List<String> findAllBizStr(String bizName);
}
