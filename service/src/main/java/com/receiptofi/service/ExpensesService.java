package com.receiptofi.service;

import com.receiptofi.domain.ExpenseTagEntity;
import com.receiptofi.repository.ExpenseTagManager;
import com.receiptofi.repository.ItemManager;
import com.receiptofi.repository.ReceiptManager;

import org.apache.commons.lang3.StringUtils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * User: hitender
 * Date: 5/23/13
 * Time: 11:49 PM
 */
@SuppressWarnings ({
        "PMD.BeanMembersShouldSerialize",
        "PMD.LocalVariableCouldBeFinal",
        "PMD.MethodArgumentCouldBeFinal",
        "PMD.LongVariable"
})
@Service
public class ExpensesService {

    @Autowired private ExpenseTagManager expenseTagManager;
    @Autowired private ReceiptManager receiptManager;
    @Autowired private ItemManager itemManager;

    /**
     * Lists all the active expense types.
     *
     * @param rid
     * @return
     */
    public List<ExpenseTagEntity> getExpenseTags(String rid) {
        return expenseTagManager.getExpenseTags(rid);
    }

    public ExpenseTagEntity findExpenseTag(String rid, String expenseId) {
        return expenseTagManager.getExpenseTag(rid, expenseId);
    }

    /**
     * Lists all the expenseTypes.
     *
     * @param rid
     * @return
     */
    public List<ExpenseTagEntity> getAllExpenseTypes(String rid) {
        return expenseTagManager.getAllExpenseTags(rid);
    }

    public ExpenseTagEntity getExpenseTag(String rid, String expenseTypeId) {
        if (StringUtils.isNotBlank(expenseTypeId)) {
            return expenseTagManager.getExpenseTag(rid, expenseTypeId);
        }
        return null;
    }

    public void saveExpenseTag(ExpenseTagEntity expenseType) {
        expenseTagManager.save(expenseType);
    }

    public void updateExpenseTag(String expenseTypeId, String expenseTagName, String expenseTagColor, String rid) {
        expenseTagManager.updateExpenseTag(expenseTypeId, expenseTagName, expenseTagColor, rid);
    }

    public void deleteExpenseTag(String expenseTypeId, String expenseTagName, String expenseTagColor, String rid) {
        receiptManager.removeExpenseTagReferences(rid, expenseTypeId);
        itemManager.removeExpenseTagReferences(rid, expenseTypeId);
        expenseTagManager.deleteExpenseTag(expenseTypeId, expenseTagName, expenseTagColor, rid);
    }
}
