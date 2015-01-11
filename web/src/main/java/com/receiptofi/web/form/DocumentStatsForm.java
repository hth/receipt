package com.receiptofi.web.form;

import java.util.Date;

/**
 * User: hitender
 * Date: 1/11/15 9:03 AM
 */
@SuppressWarnings ({
        "PMD.BeanMembersShouldSerialize",
        "PMD.LocalVariableCouldBeFinal",
        "PMD.MethodArgumentCouldBeFinal",
        "PMD.LongVariable"
})
public class DocumentStatsForm {

    private long pendingCount;
    private Date pendingCountSynced = new Date();
    private long rejectedCount;
    private Date rejectedCountSynced = new Date();

    public long getPendingCount() {
        return pendingCount;
    }

    public void setPendingCount(long pendingCount) {
        this.pendingCount = pendingCount;
    }

    public Date getPendingCountSynced() {
        return pendingCountSynced;
    }

    public void setPendingCountSynced(Date pendingCountSynced) {
        this.pendingCountSynced = pendingCountSynced;
    }

    public long getRejectedCount() {
        return rejectedCount;
    }

    public void setRejectedCount(long rejectedCount) {
        this.rejectedCount = rejectedCount;
    }

    public Date getRejectedCountSynced() {
        return rejectedCountSynced;
    }

    public void setRejectedCountSynced(Date rejectedCountSynced) {
        this.rejectedCountSynced = rejectedCountSynced;
    }
}
