package com.receiptofi.domain;

import com.receiptofi.utils.ColorUtil;
import com.receiptofi.utils.DateUtil;

import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;
import org.springframework.format.annotation.NumberFormat;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 * ExpenseTagEntity as reference field : EXPENSE_TAG.
 * User: hitender
 * Date: 5/13/13
 * Time: 7:47 PM
 */
@SuppressWarnings ({
        "PMD.BeanMembersShouldSerialize",
        "PMD.LocalVariableCouldBeFinal",
        "PMD.MethodArgumentCouldBeFinal",
        "PMD.LongVariable"
})
@Document (collection = "EXPENSE_TAG")
@CompoundIndexes (value = {
        @CompoundIndex (name = "expense_tag_idx", def = "{'RID': 1, 'TAG': 1}", unique = true),
})
public class ExpenseTagEntity extends BaseEntity {

    @NotNull
    @Size (min = 0, max = 22)
    @Field ("TAG")
    private String tagName;

    @NotNull
    @Size (max = 4)
    @Field ("Y")
    private int forYear;

    @NotNull
    @Field ("RID")
    private String receiptUserId;

    @NotNull
    @Field ("CLR")
    private String tagColor;

    //TODO add budget through expense tags
//    @NumberFormat (style = NumberFormat.Style.CURRENCY)
//    @Field ("BG")
//    private Double budget;

    /** To keep bean happy for auto populating drop down */
    public ExpenseTagEntity() {
        super();
    }

    private ExpenseTagEntity(String tagName, String receiptUserId, String tagColor) {
        super();
        this.tagName = tagName;
        this.receiptUserId = receiptUserId;
        this.forYear = DateUtil.now().getYear();
        if (null != tagColor) {
            this.tagColor = tagColor;
        } else {
            this.tagColor = ColorUtil.getRandom();
        }
    }

    public static ExpenseTagEntity newInstance(String tagName, String receiptUserId, String tagColor) {
        return new ExpenseTagEntity(tagName, receiptUserId, tagColor);
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

    public String getReceiptUserId() {
        return receiptUserId;
    }

    public void setReceiptUserId(String receiptUserId) {
        this.receiptUserId = receiptUserId;
    }

    public String getTagColor() {
        return tagColor;
    }

//    public Double getBudget() {
//        return budget;
//    }
//
//    public void setBudget(Double budget) {
//        this.budget = budget;
//    }

    @Override
    public String toString() {
        return "ExpenseTagEntity{" +
                "tagName='" + tagName + '\'' +
                ", forYear=" + forYear +
                ", receiptUserId='" + receiptUserId + '\'' +
                '}';
    }
}

