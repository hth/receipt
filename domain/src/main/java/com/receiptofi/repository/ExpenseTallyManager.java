package com.receiptofi.repository;

import com.receiptofi.domain.ExpenseTallyEntity;

import java.util.List;

/**
 * User: hitender
 * Date: 7/23/16 9:20 AM
 */
public interface ExpenseTallyManager extends RepositoryManager<ExpenseTallyEntity> {
    List<ExpenseTallyEntity> getUsersForExpenseTally(String tid, int limit);
}
