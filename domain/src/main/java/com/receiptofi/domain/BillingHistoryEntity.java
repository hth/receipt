package com.receiptofi.domain;

import com.receiptofi.domain.types.BilledStatusEnum;

import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;
import org.springframework.format.annotation.DateTimeFormat;

import java.text.SimpleDateFormat;
import java.util.Date;

import javax.validation.constraints.NotNull;

/**
 * Contains users billing history with status for that month. This status will be reflected on receipts of that month.
 *
 * User: hitender
 * Date: 3/19/15 1:32 AM
 */
@SuppressWarnings ({
        "PMD.BeanMembersShouldSerialize",
        "PMD.LocalVariableCouldBeFinal",
        "PMD.MethodArgumentCouldBeFinal",
        "PMD.LongVariable"
})
@Document (collection = "BILLING_HISTORY")
@CompoundIndexes ({@CompoundIndex (name = "billing_history_idx", def = "{'RID': 1}")})
public class BillingHistoryEntity extends BaseEntity {
    public static final SimpleDateFormat SDF = new SimpleDateFormat("YYYY-MM");

    @NotNull
    @Field ("RID")
    private String rid;

    @NotNull
    @Field ("BS")
    private BilledStatusEnum billedStatus = BilledStatusEnum.UNPAID;

    @NotNull
    @Field ("BM")
    private String billedForMonth;

    @SuppressWarnings("unused")
    private BillingHistoryEntity() {
        super();
    }

    public BillingHistoryEntity(String rid) {
        super();
        this.rid = rid;
    }

    public String getRid() {
        return rid;
    }

    public BilledStatusEnum getBilledStatus() {
        return billedStatus;
    }

    public void setBilledStatus(BilledStatusEnum billedStatus) {
        this.billedStatus = billedStatus;
    }

    public String getBilledForMonth() {
        return billedForMonth;
    }

    public void setBilledForMonth(Date billedForMonth) {
        this.billedForMonth = SDF.format(billedForMonth);
    }
}
