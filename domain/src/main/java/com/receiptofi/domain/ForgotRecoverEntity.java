package com.receiptofi.domain;

import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import javax.validation.constraints.NotNull;

/**
 * User: hitender
 * Date: 6/4/13
 * Time: 12:02 AM
 */
@SuppressWarnings ({
        "PMD.BeanMembersShouldSerialize",
        "PMD.LocalVariableCouldBeFinal",
        "PMD.MethodArgumentCouldBeFinal",
        "PMD.LongVariable"
})
@Document (collection = "FORGOT_RECOVER")
@CompoundIndexes (value = {
        @CompoundIndex (name = "forgot_recover_idx", def = "{'RID': -1, 'AUTH' : -1}", unique = true, background = true)
})
public class ForgotRecoverEntity extends BaseEntity {

    @NotNull
    @Field ("RID")
    private final String receiptUserId;

    @NotNull
    @Field ("AUTH")
    private final String authenticationKey;

    private ForgotRecoverEntity(String receiptUserId, String authenticationKey) {
        super();
        this.receiptUserId = receiptUserId;
        this.authenticationKey = authenticationKey;
    }

    public static ForgotRecoverEntity newInstance(String receiptUserId, String authenticationKey) {
        return new ForgotRecoverEntity(receiptUserId, authenticationKey);
    }

    public String getReceiptUserId() {
        return receiptUserId;
    }

    public String getAuthenticationKey() {
        return authenticationKey;
    }
}
