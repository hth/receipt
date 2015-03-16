package com.receiptofi.domain.value;

import java.math.BigDecimal;

/**
 * User: hitender
 * Date: 3/16/15 6:42 AM
 */
@SuppressWarnings ({
        "PMD.BeanMembersShouldSerialize",
        "PMD.LocalVariableCouldBeFinal",
        "PMD.MethodArgumentCouldBeFinal",
        "PMD.LongVariable"
})
public class DiskUsageGrouped {

    public static final BigDecimal MB = new BigDecimal(1_000_000);

    private long totalLN = 0;
    private long totalSLN = 0;
    private String rid;

    /**
     * Summed LN can be used to compute actual file sized uploaded.
     *
     * @return
     */
    public long getTotalLN() {
        return totalLN;
    }

    public long getTotalSLN() {
        return totalSLN;
    }

    public String getRid() {
        return rid;
    }
}
