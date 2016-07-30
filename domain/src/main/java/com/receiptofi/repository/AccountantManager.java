package com.receiptofi.repository;

import com.receiptofi.domain.AccountantEntity;

import java.util.List;

/**
 * User: hitender
 * Date: 7/23/16 9:20 AM
 */
public interface AccountantManager extends RepositoryManager<AccountantEntity> {
    List<AccountantEntity> getUsersSubscribedToAccountant(String aid, int limit);

    AccountantEntity getUserForAccountant(String rid, String auth, String aid, String ip);
}
