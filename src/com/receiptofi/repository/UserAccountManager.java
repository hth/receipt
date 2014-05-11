package com.receiptofi.repository;

import com.receiptofi.domain.BaseEntity;
import com.receiptofi.domain.UserAccountEntity;

import org.springframework.data.mongodb.core.mapping.Document;

/**
 * User: hitender
 * Date: 4/23/14 6:43 AM
 */
public interface UserAccountManager extends RepositoryManager<UserAccountEntity> {
    static String TABLE = BaseEntity.getClassAnnotationValue(UserAccountEntity.class, Document.class, "collection");

    UserAccountEntity findUserAccount(String rid);
}
