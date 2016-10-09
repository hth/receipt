package com.receiptofi.service;

import com.receiptofi.domain.ExpenseTagEntity;
import com.receiptofi.domain.annotation.Mobile;
import com.receiptofi.domain.types.ExpenseTagIconEnum;
import com.receiptofi.repository.ExpenseTagManager;
import com.receiptofi.repository.ItemManager;
import com.receiptofi.repository.ReceiptManager;
import com.receiptofi.utils.RandomString;

import org.apache.commons.lang3.StringUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

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
    private static final Logger LOG = LoggerFactory.getLogger(ExpensesService.class);

    private ExpenseTagManager expenseTagManager;
    private ReceiptManager receiptManager;
    private ItemManager itemManager;

    @Autowired
    public ExpensesService(
            ExpenseTagManager expenseTagManager,
            ReceiptManager receiptManager,
            ItemManager itemManager) {
        this.expenseTagManager = expenseTagManager;
        this.receiptManager = receiptManager;
        this.itemManager = itemManager;
    }

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
     * Lists all the expenseTypes including deleted and not visible.
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

    @Mobile
    public void saveExpenseTag(ExpenseTagEntity expenseTag) {
        ExpenseTagEntity expenseTagExists = expenseTagManager.getExpenseTagByName(expenseTag.getReceiptUserId(), expenseTag.getTagName());
        if (expenseTagExists == null) {
            expenseTagManager.save(expenseTag);
        } else {
            //TODO(hth) prefer to delete inactive expenseTag instead.
            LOG.warn("Previously existing expense tag by name={}", expenseTagExists.getTagName());
            expenseTagExists.setTagName(expenseTagExists.getTagName() + "-" + RandomString.newInstance(3).nextString());
            expenseTagManager.save(expenseTagExists);

            LOG.warn("Inactive expense tag saved by new name={} and now saving new expense tag with name={}",
                    expenseTagExists.getTagName(), expenseTag.getTagName());
            expenseTagManager.save(expenseTag);
        }
    }

    @Mobile
    public void updateExpenseTag(String expenseTypeId, String expenseTagName, String expenseTagColor, ExpenseTagIconEnum expenseTagIcon, String rid) {
        ExpenseTagEntity expenseTag = expenseTagManager.getExpenseTagByName(rid, expenseTagName);
        if (expenseTag == null) {
            expenseTagManager.updateExpenseTag(expenseTypeId, expenseTagName, expenseTagColor, expenseTagIcon, rid);
        } else {
            //TODO(hth) prefer to delete inactive expenseTag instead.
            LOG.warn("Previously existing expense tag by name={}", expenseTag.getTagName());
            expenseTag.setTagName(expenseTag.getTagName() + "-" + RandomString.newInstance(3).nextString());
            expenseTagManager.save(expenseTag);

            LOG.warn("Inactive expense tag saved by new name={} and now updating existing expense tag with name={}",
                    expenseTag.getTagName(), expenseTagName);
            expenseTagManager.updateExpenseTag(expenseTypeId, expenseTagName, expenseTagColor, expenseTagIcon, rid);
        }
    }

    @Mobile
    public boolean softDeleteExpenseTag(String expenseTypeId, String expenseTagName, String rid) {
        boolean removedFromReceipts = receiptManager.removeExpenseTagReferences(rid, expenseTypeId);
        boolean removedFromItems = itemManager.removeExpenseTagReferences(rid, expenseTypeId);

        LOG.info("Deleted expense tags from receipt={} and items={}", removedFromReceipts, removedFromItems);
        return expenseTagManager.softDeleteExpenseTag(expenseTypeId, expenseTagName, rid);
    }

    public void deleteHard(ExpenseTagEntity expenseTag, DataIntegrityViolationException e) {
        Assert.notNull(e, "DataIntegrityViolationException is not set or not invoked properly");
        expenseTagManager.deleteHard(expenseTag);
    }
}
