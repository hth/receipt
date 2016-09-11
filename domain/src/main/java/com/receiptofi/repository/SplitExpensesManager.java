package com.receiptofi.repository;

import com.receiptofi.domain.SplitExpensesEntity;

import java.util.List;

/**
 * User: hitender
 * Date: 9/27/15 2:37 AM
 */
public interface SplitExpensesManager extends RepositoryManager<SplitExpensesEntity> {

    SplitExpensesEntity getById(String id, String rid);

    /**
     * Delete SplitExpense only when split expenses are un-settled.
     *
     * @param rdid
     * @param rid
     * @param fid
     * @return
     */
    boolean deleteHard(String rdid, String rid, String fid);

    List<SplitExpensesEntity> getSplitExpensesFriendsForReceipt(String rdid);

    boolean doesExists(String rdid, String rid, String fid);

    List<SplitExpensesEntity> getOwesMe(String rid);

    List<SplitExpensesEntity> getOwesOthers(String rid);

    boolean updateSplitTotal(String rdid, Double splitTotal);

    List<SplitExpensesEntity> getSplitExpenses(String rid, String fid);

    SplitExpensesEntity findSplitExpensesToSettle(String fid, String rid, Double splitTotal);

    /**
     * Checks if any of the split shared with same rdid has been anything other then Unsettled.
     *
     * @param rdid
     * @return
     */
    boolean hasSettleProcessStarted(String rdid);

    List<SplitExpensesEntity> getAll();
}
