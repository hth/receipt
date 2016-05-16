package com.receiptofi.domain;

import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import javax.validation.constraints.NotNull;

/**
 * User: hitender
 * Date: 5/16/16 11:45 AM
 */
@SuppressWarnings ({
        "PMD.BeanMembersShouldSerialize",
        "PMD.LocalVariableCouldBeFinal",
        "PMD.MethodArgumentCouldBeFinal",
        "PMD.LongVariable"
})
@Document (collection = "BUSINESS_USER")
@CompoundIndexes (value = {
        @CompoundIndex (name = "business_user_idx", def = "{'RID': -1}", unique = true),
})
public class BusinessUserEntity extends BaseEntity {

    @NotNull
    @Field ("RID")
    private String receiptUserId;

    @DBRef
    @Field ("BIZ_NAME")
    private BizNameEntity bizName;

    public String getReceiptUserId() {
        return receiptUserId;
    }

    public BusinessUserEntity setReceiptUserId(String receiptUserId) {
        this.receiptUserId = receiptUserId;
        return this;
    }

    public BizNameEntity getBizName() {
        return bizName;
    }

    public BusinessUserEntity setBizName(BizNameEntity bizName) {
        this.bizName = bizName;
        return this;
    }
}
