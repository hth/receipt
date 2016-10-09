package com.receiptofi.repository;

import com.receiptofi.domain.ExpenseTagEntity;
import com.receiptofi.domain.types.ExpenseTagIconEnum;

import java.util.List;

/**
 * User: hitender
 * Date: 5/13/13
 * Time: 11:59 PM
 */
public interface ExpenseTagManager extends RepositoryManager<ExpenseTagEntity> {

    List<ExpenseTagEntity> getAllExpenseTags(String rid);

    /**
     * Gets all active expense tag
     *
     * @param rid
     * @return
     */
    List<ExpenseTagEntity> getExpenseTags(String rid);

    ExpenseTagEntity getExpenseTag(String rid, String expenseTagId);

    ExpenseTagEntity getExpenseTagByName(String rid, String expenseTagName);

    void updateExpenseTag(String expenseTagId, String expenseTagName, String expenseTagColor, ExpenseTagIconEnum expenseTagIcon, String rid);

    boolean softDeleteExpenseTag(String expenseTagId, String expenseTagName, String rid);
}
