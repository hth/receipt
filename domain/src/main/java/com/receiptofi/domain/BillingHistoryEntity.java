package com.receiptofi.domain;

import com.receiptofi.domain.types.AccountBillingTypeEnum;
import com.receiptofi.domain.types.BilledStatusEnum;
import com.receiptofi.domain.types.TransactionStatusEnum;
import com.receiptofi.domain.types.PaymentGatewayEnum;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Contains users billing history with status for that month. This status will be reflected on receipts of that month.
 * Each instance if billing history reflects a single transaction. This means each transaction id must have a billing
 * history.
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
        @CompoundIndex (name = "billing_history_rid_bm_idx", def = "{'RID': 1, 'BM': -1}")
})
public class BillingHistoryEntity extends BaseEntity {
    private static final Logger LOG = LoggerFactory.getLogger(BillingHistoryEntity.class);

    public static final SimpleDateFormat YYYY_MM = new SimpleDateFormat("yyyy-MM");
    private static final SimpleDateFormat MMM_YYYY = new SimpleDateFormat("MMM, yyyy");

    @Field ("RID")
    private String rid;

    @Field ("BS")
    private BilledStatusEnum billedStatus = BilledStatusEnum.NB;

    @Field ("ABT")
    private AccountBillingTypeEnum accountBillingType;

    @Field ("BM")
    private String billedForMonth;

    @Field ("TX")
    private String transactionId;

    @Field ("PG")
    private PaymentGatewayEnum paymentGateway;

    @Field ("TS")
    private TransactionStatusEnum transactionStatus = TransactionStatusEnum.N;

    @SuppressWarnings("unused")
    private BillingHistoryEntity() {
        super();
    }

    public BillingHistoryEntity(String rid, Date billedForMonth) {
        super();
        this.rid = rid;
        this.billedForMonth = YYYY_MM.format(billedForMonth);
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

    public AccountBillingTypeEnum getAccountBillingType() {
        return accountBillingType;
    }

    public void setAccountBillingType(AccountBillingTypeEnum accountBillingType) {
        this.accountBillingType = accountBillingType;
    }

    public String getBilledForMonth() {
        return billedForMonth;
    }

    public void setBilledForMonth(Date billedForMonth) {
        this.billedForMonth = YYYY_MM.format(billedForMonth);
    }

    public String getBilledForMonthYear() {
        try {
            return MMM_YYYY.format(YYYY_MM.parse(billedForMonth));
        } catch (ParseException e) {
            LOG.error("Date parsing date={} reason={}", billedForMonth, e.getLocalizedMessage(), e);
            return "Missing";
        }
    }

    public String getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }

    public PaymentGatewayEnum getPaymentGateway() {
        return paymentGateway;
    }

    public void setPaymentGateway(PaymentGatewayEnum paymentGateway) {
        this.paymentGateway = paymentGateway;
    }

    public TransactionStatusEnum getTransactionStatus() {
        return transactionStatus;
    }

    public void setTransactionStatus(TransactionStatusEnum transactionStatus) {
        this.transactionStatus = transactionStatus;
    }
}
