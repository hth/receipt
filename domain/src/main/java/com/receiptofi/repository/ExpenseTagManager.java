package com.receiptofi.repository;

import com.receiptofi.domain.ExpenseTagEntity;

import java.util.List;

/**
 * User: hitender
 * Date: 5/13/13
 * Time: 11:59 PM
 */
public interface ExpenseTagManager extends RepositoryManager<ExpenseTagEntity> {

    List<ExpenseTagEntity> allExpenseTypes(String receiptUserId);

    /**
     * Gets all active expense tag
     *
     * @param receiptUserId
     * @return
     */
    List<ExpenseTagEntity> activeExpenseTypes(String receiptUserId);

    void changeVisibility(String expenseTypeId, boolean changeTo, String receiptUserId);

    void updateExpenseTag(String expenseTypeId, String expenseTagName, String expenseTagColor, String rid);

    void deleteExpenseTag(String expenseTypeId, String expenseTagName, String expenseTagColor, String rid);
}
