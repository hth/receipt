package com.tholix.repository;

import org.springframework.data.mongodb.core.mapping.Document;

import com.tholix.domain.BaseEntity;
import com.tholix.domain.InviteEntity;

/**
 * User: hitender
 * Date: 6/9/13
 * Time: 2:15 PM
 */
public interface InviteManager extends RepositoryManager<InviteEntity> {
    public static String TABLE = BaseEntity.getClassAnnotationValue(InviteEntity.class, Document.class, "collection");

    /**
     * Find InviteEntity by authentication key
     *
     * @param key
     * @return
     */
    InviteEntity findByAuthenticationKey(String key);

    /**
     * Make all the existing request invalid
     *
     * @param object
     */
    void invalidateAllEntries(InviteEntity object);
}
