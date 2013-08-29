package com.tholix.domain.value;

import java.io.Serializable;
import java.math.BigDecimal;
import java.text.NumberFormat;

import com.tholix.domain.BizNameEntity;
import com.tholix.domain.BizStoreEntity;

/**
 * User: hitender
 * Date: 8/27/13 8:04 PM
 */
public class ReceiptGroupedByBizLocation implements Serializable {

    private BigDecimal total;
    private BizNameEntity bizName;
    private BizStoreEntity bizStore;

    @SuppressWarnings("unused")
    private ReceiptGroupedByBizLocation() {}

    private ReceiptGroupedByBizLocation(BigDecimal total) {
        this.total = total;
    }

    public BigDecimal getTotal() {
        return total;
    }

    public void setTotal(BigDecimal total) {
        this.total = total;
    }

    public String getTotalStr() {
        NumberFormat fmt = NumberFormat.getCurrencyInstance();
       return fmt.format(getTotal());
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
