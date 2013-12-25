package com.receiptofi.domain;

import com.receiptofi.utils.DateUtil;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

/**
 * User: hitender
 * Date: 5/13/13
 * Time: 7:47 PM
 */
@Document(collection = "EXPENSE_TAG")
@CompoundIndexes(value = {
        @CompoundIndex(name = "expense_type_idx",    def = "{'USER_PROFILE_ID': 1, 'TAG': 1}",  unique=true),
} )
public class ExpenseTagEntity extends BaseEntity {

    @NotNull
    @Size(min = 0, max = 6)
    @Field("TAG")
    private String tagName;

    @NotNull
    @Size(max = 4)
    @Field("YEAR")
    private int forYear;

    @NotNull
    @Field("USER_PROFILE_ID")
    private String userProfileId;

    /** To make bean happy */
    public ExpenseTagEntity() {}

    public static ExpenseTagEntity newInstance(String expName, String userProfileId) {
        ExpenseTagEntity expenseTagEntity = new ExpenseTagEntity();
        expenseTagEntity.setTagName(expName);
        expenseTagEntity.setUserProfile(userProfileId);
        expenseTagEntity.setForYear(DateUtil.now().getYear());
        return expenseTagEntity;
    }

    public String getTagName() {
        return tagName;
    }

    public void setTagName(String tagName) {
        this.tagName = tagName;
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
