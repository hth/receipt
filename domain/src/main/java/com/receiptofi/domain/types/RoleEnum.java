package com.receiptofi.domain.types;

/**
 * Define roles of each user.
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

    /** Validate and process data */
    ROLE_TECHNICIAN,

    /** Has read access */
    ROLE_ANALYSIS_READ,

    /** Has complete access */
    ROLE_SUPERVISOR,

    /** Has administrator role */
    ROLE_ADMIN
}
