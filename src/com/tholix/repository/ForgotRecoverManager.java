package com.tholix.repository;

import org.springframework.data.mongodb.core.mapping.Document;

import com.tholix.domain.BaseEntity;
import com.tholix.domain.ForgotRecoverEntity;

/**
 * User: hitender
 * Date: 6/4/13
 * Time: 12:10 AM
 */
public interface ForgotRecoverManager extends RepositoryManager<ForgotRecoverEntity> {
    public static String TABLE = BaseEntity.getClassAnnotationValue(ForgotRecoverEntity.class, Document.class, "collection");

    /**
     * Find ForgotRecoverEntity by authentication key
     *
     * @param key
     * @return
     */
    ForgotRecoverEntity findByAuthenticationKey(String key);

    /**
     * Make all the existing request invalid
     *
     * @param object
     */
    void invalidateAllPreviousEntries(ForgotRecoverEntity object);
}
