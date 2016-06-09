package com.receiptofi.domain;

import com.receiptofi.domain.types.BusinessUserRegistrationStatusEnum;

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

    @NotNull
    @Field ("RS")
    private BusinessUserRegistrationStatusEnum businessUserRegistrationStatus;

    @NotNull
    @Field ("VB")
    private String validateByRid;

    @DBRef
    @Field ("BIZ_NAME")
    private BizNameEntity bizName;

    @SuppressWarnings("unused")
    private BusinessUserEntity() {
        super();
    }

    private BusinessUserEntity(String receiptUserId) {
        super();
        this.receiptUserId = receiptUserId;

        /** When creating this record we are defaulting to Incomplete status. */
        this.businessUserRegistrationStatus = BusinessUserRegistrationStatusEnum.I;
    }

    public static BusinessUserEntity newInstance(String receiptUserId) {
        return new BusinessUserEntity(receiptUserId);
    }

    public String getReceiptUserId() {
        return receiptUserId;
    }

    public BusinessUserEntity setReceiptUserId(String receiptUserId) {
        this.receiptUserId = receiptUserId;
        return this;
    }

    public BusinessUserRegistrationStatusEnum getBusinessUserRegistrationStatus() {
        return businessUserRegistrationStatus;
    }

    public BusinessUserEntity setBusinessUserRegistrationStatus(BusinessUserRegistrationStatusEnum businessUserRegistrationStatus) {
        this.businessUserRegistrationStatus = businessUserRegistrationStatus;
        return this;
    }

    public String getValidateByRid() {
        return validateByRid;
    }

    public BusinessUserEntity setValidateByRid(String validateByRid) {
        this.validateByRid = validateByRid;
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
