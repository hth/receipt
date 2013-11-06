package com.receiptofi.domain;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import com.receiptofi.utils.DateUtil;

/**
 * User: hitender
 * Date: 5/13/13
 * Time: 7:47 PM
 */
@Document(collection = "EXPENSE_TYPE")
@CompoundIndexes(value = {
        @CompoundIndex(name = "expense_type_idx",    def = "{'USER_PROFILE_ID': 1, 'EXP_NAME': 1}",  unique=true),
} )
public class ExpenseTypeEntity extends BaseEntity {

    @NotNull
    @Size(min = 0, max = 6)
    @Field("EXP_NAME")
    private String expName;

    @NotNull
    @Size(max = 4)
    @Field("YEAR")
    private int forYear;

    @NotNull
    @Field("USER_PROFILE_ID")
    private String userProfileId;

    /** To make bean happy */
    public ExpenseTypeEntity() {}

    public static ExpenseTypeEntity newInstance(String expName, String userProfileId) {
        ExpenseTypeEntity expenseTypeEntity = new ExpenseTypeEntity();
        expenseTypeEntity.setExpName(expName);
        expenseTypeEntity.setUserProfile(userProfileId);
        expenseTypeEntity.setForYear(DateUtil.now().getYear());
        return expenseTypeEntity;
    }

    public String getExpName() {
        return expName;
    }

    public void setExpName(String expName) {
        this.expName = expName;
    }

    public int getForYear() {
        return forYear;
    }

    public void setForYear(int forYear) {
        this.forYear = forYear;
    }

    public String getUserProfileId() {
        return userProfileId;
    }

    public void setUserProfile(String userProfileId) {
        this.userProfileId = userProfileId;
    }
}
