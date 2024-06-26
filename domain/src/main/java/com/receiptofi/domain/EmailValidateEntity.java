package com.receiptofi.domain;

import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import javax.validation.constraints.NotNull;

/**
 * User: hitender
 * Date: 5/17/14 4:49 PM
 */
@SuppressWarnings ({
        "PMD.BeanMembersShouldSerialize",
        "PMD.LocalVariableCouldBeFinal",
        "PMD.MethodArgumentCouldBeFinal",
        "PMD.LongVariable"
})
@Document (collection = "EMAIL_VALIDATE")
@CompoundIndexes (value = {
        @CompoundIndex (name = "email_valid_idx", def = "{'AUTH': 1}", unique = true),
})
public class EmailValidateEntity extends BaseEntity {

    @NotNull
    @Field ("RID")
    private String receiptUserId;

    @NotNull
    @Field ("EM")
    private String email;

    @NotNull
    @Field ("AUTH")
    private String authenticationKey;

    private EmailValidateEntity(String receiptUserId, String email, String authenticationKey) {
        super();
        this.receiptUserId = receiptUserId;
        this.email = email;
        this.authenticationKey = authenticationKey;
    }

    public static EmailValidateEntity newInstance(String receiptUserId, String email, String authenticationKey) {
        return new EmailValidateEntity(receiptUserId, email, authenticationKey);
    }

    public String getReceiptUserId() {
        return receiptUserId;
    }

    public String getEmail() {
        return email;
    }

    public String getAuthenticationKey() {
        return authenticationKey;
    }
}
