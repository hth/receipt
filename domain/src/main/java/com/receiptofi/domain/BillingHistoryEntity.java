package com.receiptofi.domain;

import com.receiptofi.domain.types.BilledStatusEnum;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;
import org.springframework.format.annotation.DateTimeFormat;

import java.text.ParseException;
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
@CompoundIndexes (value = {
        @CompoundIndex (name = "billing_history_rid_idx", def = "{'RID': 1}"),
        @CompoundIndex (name = "billing_history_rid_bm_idx", def = "{'RID': 1, 'BM': -1}", unique = true)
})
public class BillingHistoryEntity extends BaseEntity {
    private static final Logger LOG = LoggerFactory.getLogger(BillingHistoryEntity.class);

    public static final SimpleDateFormat SDF = new SimpleDateFormat("yyyy-MM");
    private static final SimpleDateFormat SDF_MMM_YYYY = new SimpleDateFormat("MMM, yyyy");

    @NotNull
    @Field ("RID")
    private String rid;

    @NotNull
    @Field ("BS")
    private BilledStatusEnum billedStatus = BilledStatusEnum.NB;

    @NotNull
    @Field ("BM")
    private String billedForMonth;

    @SuppressWarnings("unused")
    private BillingHistoryEntity() {
        super();
    }

    public BillingHistoryEntity(String rid, Date billedForMonth) {
        super();
        this.rid = rid;
        this.billedForMonth = SDF.format(billedForMonth);
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

    public String getBilledForMonthYear() {
        try {
            return SDF_MMM_YYYY.format(SDF.parse(billedForMonth));
        } catch (ParseException e) {
            LOG.error("Date parsing date={} reason={}", billedForMonth, e.getLocalizedMessage(), e);
            return "Not Available";
        }
    }
}
