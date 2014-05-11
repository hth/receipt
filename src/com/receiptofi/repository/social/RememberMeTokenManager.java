package com.receiptofi.repository.social;

import com.receiptofi.domain.BaseEntity;
import com.receiptofi.domain.social.RememberMeTokenEntity;
import com.receiptofi.repository.RepositoryManager;

import java.util.Date;
import java.util.List;

import org.springframework.data.mongodb.core.mapping.Document;

/**
 * User: hitender
 * Date: 3/30/14 7:38 PM
 */
public interface RememberMeTokenManager extends RepositoryManager<RememberMeTokenEntity> {
    public static String TABLE = BaseEntity.getClassAnnotationValue(RememberMeTokenEntity.class, Document.class, "collection");

    RememberMeTokenEntity findBySeries(String series);
    void deleteTokensWithUsername(String username);
}
