package com.receiptofi.domain.types;

/**
 * Represent what has been recently updated on server. This would normally be used in updating mobile app.
 * User: hitender
 * Date: 8/9/14 2:41 PM
 */
@SuppressWarnings ({
        "PMD.BeanMembersShouldSerialize",
        "PMD.LocalVariableCouldBeFinal",
        "PMD.MethodArgumentCouldBeFinal",
        "PMD.LongVariable"
})
public enum RecentActivityEnum {
    RECEIPT,
    MILEAGE,
    PROFILE,
    UPLOAD_DOCUMENT
}
