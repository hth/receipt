package com.receiptofi.domain.types;

/**
 * Define roles of each user. These are set for @PreAuthorize to access specific
 * services defined for user. Roles are set based on UserLevel.
 * User: hitender
 * Date: 4/12/14 11:05 AM
 */
@SuppressWarnings ({
        "PMD.BeanMembersShouldSerialize",
        "PMD.LocalVariableCouldBeFinal",
        "PMD.MethodArgumentCouldBeFinal",
        "PMD.LongVariable"
})
public enum RoleEnum {
    /** A regular user */
    ROLE_USER,

    /** A business user */
    ROLE_BUSINESS,

    /** An enterprise user who has multiple receipt user */
    ROLE_ENTERPRISE,

    /** Validate and process data */
    ROLE_TECHNICIAN,

    /** Has read access */
    ROLE_ANALYSIS_READ,

    /** Has complete access */
    ROLE_SUPERVISOR,

    /** Has administrator role */
    ROLE_ADMIN
}
