package com.receiptofi.domain;

import com.receiptofi.domain.types.BusinessUserRegistrationStatusEnum;
import com.receiptofi.domain.types.UserLevelEnum;

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

    /* Set the kind of business is registered as. */
    @Field ("UL")
    private UserLevelEnum userLevel;

    @SuppressWarnings("unused")
    private BusinessUserEntity() {
        super();
    }

    private BusinessUserEntity(String receiptUserId, UserLevelEnum userLevel) {
        super();
        this.receiptUserId = receiptUserId;
        this.userLevel = userLevel;

        /** When creating this record we are defaulting to Incomplete status. */
        this.businessUserRegistrationStatus = BusinessUserRegistrationStatusEnum.I;
    }

    public static BusinessUserEntity newInstance(String receiptUserId, UserLevelEnum userLevel) {
        return new BusinessUserEntity(receiptUserId, userLevel);
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
