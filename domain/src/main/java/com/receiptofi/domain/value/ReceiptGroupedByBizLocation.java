package com.receiptofi.domain.value;

import com.receiptofi.domain.BizNameEntity;
import com.receiptofi.domain.BizStoreEntity;

import java.io.Serializable;
import java.math.BigDecimal;
import java.text.NumberFormat;

/**
 * TODO(hth) this class can be further extended to individually list out the expense in that store on a particular date.
 * User: hitender
 * Date: 8/27/13 8:04 PM
 */
@SuppressWarnings ({
        "PMD.BeanMembersShouldSerialize",
        "PMD.LocalVariableCouldBeFinal",
        "PMD.MethodArgumentCouldBeFinal",
        "PMD.LongVariable"
})
public class ReceiptGroupedByBizLocation implements Serializable {

    private BigDecimal splitTotal;
    private BizNameEntity bizName;
    private BizStoreEntity bizStore;

    @SuppressWarnings ("unused")
    private ReceiptGroupedByBizLocation() {
        super();
    }

    @SuppressWarnings ("unused")
    private ReceiptGroupedByBizLocation(BigDecimal splitTotal) {
        super();
        this.splitTotal = splitTotal;
    }

    public BigDecimal getSplitTotal() {
        return splitTotal;
    }

    public void setSplitTotal(BigDecimal splitTotal) {
        this.splitTotal = splitTotal;
    }

    public String getTotalStr() {
        NumberFormat fmt = NumberFormat.getCurrencyInstance();
        return fmt.format(splitTotal);
    }

    public BizNameEntity getBizName() {
        return bizName;
    }

    public void setBizName(BizNameEntity bizName) {
        this.bizName = bizName;
    }

    public BizStoreEntity getBizStore() {
        return bizStore;
    }

    public void setBizStore(BizStoreEntity bizStore) {
        this.bizStore = bizStore;
    }
}
