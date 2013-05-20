package com.tholix.domain;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import org.springframework.data.mongodb.core.mapping.Document;

import com.tholix.utils.DateUtil;

/**
 * User: hitender
 * Date: 5/13/13
 * Time: 7:47 PM
 */
@Document(collection = "EXPENSE_TYPE")
@CompoundIndexes(value = {
        @CompoundIndex(name = "expense_type_idx",    def = "{'userProfileId': 1, 'expName': 1}",  unique=true),
} )

public class ExpenseTypeEntity extends BaseEntity {

    @NotNull
    @Size(min = 0, max = 12)
    private String expName;

    @NotNull
    @Size(max = 4)
    private int forYear;

    @NotNull
    private String userProfileId;

    /** To make bean happy */
    public ExpenseTypeEntity() {

    }

    private ExpenseTypeEntity(String expName, String userProfileId) {
        super();
        this.expName = expName;
        this.userProfileId = userProfileId;
        this.forYear = DateUtil.now().getYear();
    }

    public static ExpenseTypeEntity newInstance(String expName, String userProfileId) {
        return new ExpenseTypeEntity(expName, userProfileId);
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
