package com.tholix.repository;

import java.util.List;

import org.springframework.data.mongodb.core.mapping.Document;

import com.tholix.domain.BaseEntity;
import com.tholix.domain.ExpenseTypeEntity;

/**
 * User: hitender
 * Date: 5/13/13
 * Time: 11:59 PM
 */
public interface ExpenseTypeManager extends RepositoryManager<ExpenseTypeEntity> {
    public static String TABLE = BaseEntity.getClassAnnotationValue(ExpenseTypeEntity.class, Document.class, "collection");

    List<ExpenseTypeEntity> allExpenseTypes(String userProfileId);

    void changeVisibility(String expenseTypeId, boolean changeTo);
}
