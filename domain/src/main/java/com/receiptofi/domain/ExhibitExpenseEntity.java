package com.receiptofi.domain;

import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.Date;
import java.util.List;

import javax.validation.constraints.NotNull;

/**
 * Uni directional exhibition of RID expenses with EID.
 * User: hitender
 * Date: 7/23/16 8:35 AM
 */
@SuppressWarnings ({
        "PMD.BeanMembersShouldSerialize",
        "PMD.LocalVariableCouldBeFinal",
        "PMD.MethodArgumentCouldBeFinal",
        "PMD.LongVariable"
})
@Document (collection = "EXHIBIT_EXPENSE")
@CompoundIndexes (value = {
        @CompoundIndex (
                name = "exhibit_expense_rid_eid_idx",
                def = "{'RID': 1, 'EID': 1}",
                background = true,
                unique = true)
})
public class ExhibitExpenseEntity extends BaseEntity {

    @NotNull
    @Field ("RID")
    private String receiptUserId;

    @NotNull
    @Field ("EID")
    private String exhibitUserId;

    @NotNull
    @Field ("EC")
    private String exhibitCode;

    @Field ("ET")
    private List<String> expenseTags;

    /* Delay sharing receipt by number of days. */
    @Field ("DE")
    private int delayExhibit;

    /* Day when connection was accepted. */
    @Field ("CD")
    private Date connected;

    private ExhibitExpenseEntity(String receiptUserId, String exhibitUserId, String exhibitCode) {
        this.receiptUserId = receiptUserId;
        this.exhibitUserId = exhibitUserId;
        this.exhibitCode = exhibitCode;
    }

    public static ExhibitExpenseEntity newInstance(String receiptUserId, String exhibitUserId, String exhibitCode) {
        return new ExhibitExpenseEntity(receiptUserId, exhibitUserId, exhibitCode);
    }

    public String getReceiptUserId() {
        return receiptUserId;
    }

    public String getExhibitUserId() {
        return exhibitUserId;
    }

    public String getExhibitCode() {
        return exhibitCode;
    }

    public void setExhibitCode(String exhibitCode) {
        this.exhibitCode = exhibitCode;
    }

    public List<String> getExpenseTags() {
        return expenseTags;
    }

    public void setExpenseTags(List<String> expenseTags) {
        this.expenseTags = expenseTags;
    }

    public int getDelayExhibit() {
        return delayExhibit;
    }

    public void setDelayExhibit(int delayExhibit) {
        this.delayExhibit = delayExhibit;
    }

    public Date getConnected() {
        return connected;
    }

    public void setConnected(Date connected) {
        this.connected = connected;
    }

    @Override
    public String toString() {
        return "ExhibitExpenseEntity{" +
                "receiptUserId='" + receiptUserId + '\'' +
                ", exhibitUserId='" + exhibitUserId + '\'' +
                ", exhibitCode='" + exhibitCode + '\'' +
                '}';
    }
}
