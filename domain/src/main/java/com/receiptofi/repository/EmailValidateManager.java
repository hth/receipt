package com.receiptofi.repository;

import com.receiptofi.domain.EmailValidateEntity;

/**
 * User: hitender
 * Date: 5/17/14 6:29 PM
 */
public interface EmailValidateManager extends RepositoryManager<EmailValidateEntity> {
    EmailValidateEntity findByAuthenticationKey(String auth);

    void invalidateAllEntries(String receiptUserId);

    EmailValidateEntity find(String email);
}
