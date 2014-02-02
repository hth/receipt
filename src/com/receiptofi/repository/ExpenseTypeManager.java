package com.receiptofi.repository;

import com.receiptofi.domain.BaseEntity;
import com.receiptofi.domain.ExpenseTagEntity;

import java.util.List;

import org.springframework.data.mongodb.core.mapping.Document;

/**
 * User: hitender
 * Date: 5/13/13
 * Time: 11:59 PM
 */
public interface ExpenseTypeManager extends RepositoryManager<ExpenseTagEntity> {
    public static String TABLE = BaseEntity.getClassAnnotationValue(ExpenseTagEntity.class, Document.class, "collection");

    List<ExpenseTagEntity> allExpenseTypes(String userProfileId);

    /**
     * Gets all active expense tag
     *
     * @param userProfileId
     * @return
     */
    List<ExpenseTagEntity> activeExpenseTypes(String userProfileId);

    void changeVisibility(String expenseTypeId, boolean changeTo, String userProfileId);
}
