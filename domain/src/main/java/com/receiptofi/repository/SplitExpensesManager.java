package com.receiptofi.repository;

import com.receiptofi.domain.SplitExpensesEntity;

import java.util.List;

/**
 * User: hitender
 * Date: 9/27/15 2:37 AM
 */
public interface SplitExpensesManager extends RepositoryManager<SplitExpensesEntity> {

    boolean deleteHard(String rdid, String rid, String fid);

    List<SplitExpensesEntity> getSplitExpensesFriendsForReceipt(String rdid);

    boolean doesExists(String rdid, String rid, String fid);
}
