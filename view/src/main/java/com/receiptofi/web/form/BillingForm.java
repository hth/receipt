package com.receiptofi.web.form;

import com.receiptofi.domain.BillingHistoryEntity;
import com.receiptofi.domain.types.BillingPlanEnum;
import com.receiptofi.domain.value.DiskUsageGrouped;
import com.receiptofi.utils.Maths;

import org.springframework.format.annotation.NumberFormat;

import java.math.BigDecimal;
import java.util.List;

/**
 * User: hitender
 * Date: 3/20/15 1:01 PM
 */
public class BillingForm {
    private DiskUsageGrouped diskUsage;
    private long pendingDiskUsage;
    private List<BillingHistoryEntity> billings;

    private BillingPlanEnum billingPlan;

    public void setDiskUsage(DiskUsageGrouped diskUsage) {
        this.diskUsage = diskUsage;
    }

    @NumberFormat (style = NumberFormat.Style.NUMBER)
    public BigDecimal getTotalSLN_MB() {
        return Maths.divide(diskUsage.getTotalSLN(), DiskUsageGrouped.MB);
    }

    @NumberFormat (style = NumberFormat.Style.NUMBER)
    public BigDecimal getPendingDiskUsage_MB() {
        return Maths.divide(pendingDiskUsage, DiskUsageGrouped.MB);
    }

    /**
     * Saved space result of file scaling.
     *
     * @return
     */
    @NumberFormat (style = NumberFormat.Style.NUMBER)
    public BigDecimal getDiskSaved_MB() {
        return Maths.divide(diskUsage.getTotalLN() - pendingDiskUsage - diskUsage.getTotalSLN(), DiskUsageGrouped.MB);
    }

    public void setPendingDiskUsage(long pendingDiskUsage) {
        this.pendingDiskUsage = pendingDiskUsage;
    }

    public List<BillingHistoryEntity> getBillings() {
        return billings;
    }

    public void setBillings(List<BillingHistoryEntity> billings) {
        this.billings = billings;
    }

    public BillingPlanEnum getBillingPlan() {
        return billingPlan;
    }

    public void setBillingPlan(BillingPlanEnum billingPlan) {
        this.billingPlan = billingPlan;
    }
}
