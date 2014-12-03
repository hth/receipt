package com.receiptofi.domain.types;

/**
 * Used in UI to display appropriate messages to user if password recovery email is sent or account validation email is
 * sent.
 * User: hitender
 * Date: 11/15/14 3:52 PM
 */
@SuppressWarnings ({
        "PMD.BeanMembersShouldSerialize",
        "PMD.LocalVariableCouldBeFinal",
        "PMD.MethodArgumentCouldBeFinal",
        "PMD.LongVariable"
})
public enum MailTypeEnum {
    FAILURE, SUCCESS, ACCOUNT_NOT_VALIDATED
}
