package com.receiptofi.web.form;

import com.receiptofi.domain.BizNameEntity;
import com.receiptofi.domain.BizStoreEntity;

import org.apache.commons.lang3.StringUtils;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * User: hitender
 * Date: 4/22/13
 * Time: 11:52 PM
 */
@SuppressWarnings ({
        "PMD.BeanMembersShouldSerialize",
        "PMD.LocalVariableCouldBeFinal",
        "PMD.MethodArgumentCouldBeFinal",
        "PMD.LongVariable"
})
public final class BizForm {
    private String businessName;
    private String address;
    private String phone;

    private String nameId;
    private String bizStoreId;

    private Set<BizStoreEntity> last10BizStore;
    private Map<String, Long> receiptCount = new HashMap<>();
    private BizStoreEntity addedBizStore;

    private String errorMessage;
    private String successMessage;

    /** To make bean happy */
    private BizForm() {
    }

    public static BizForm newInstance() {
        return new BizForm();
    }

    public String getBusinessName() {
        return businessName;
    }

    public void setBusinessName(String name) {
        this.businessName = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getNameId() {
        return nameId;
    }

    public void setNameId(String nameId) {
        this.nameId = nameId;
    }

    public String getBizStoreId() {
        return bizStoreId;
    }

    public void setBizStoreId(String bizStoreId) {
        this.bizStoreId = bizStoreId;
    }

    public void setBizNameEntity(BizNameEntity bizName) {
        this.businessName = bizName.getBusinessName();
        this.nameId = bizName.getId();
    }

    public void setBizStore(BizStoreEntity bizStore) {
        this.address = bizStore.getAddress();
        this.bizStoreId = bizStore.getId();
        this.phone = bizStore.getPhoneFormatted();
    }

    public Set<BizStoreEntity> getLast10BizStore() {
        return last10BizStore;
    }

    public void setLast10BizStore(Set<BizStoreEntity> last10BizStore) {
        this.last10BizStore = last10BizStore;
    }

    public Map<String, Long> getReceiptCount() {
        return receiptCount;
    }

    public void setReceiptCount(Map<String, Long> receiptCount) {
        this.receiptCount = receiptCount;
    }

    /** To be used in future for sending confirmation regarding adding Biz Name and Store success or failure */
    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        if (StringUtils.isEmpty(this.errorMessage)) {
            this.errorMessage = errorMessage;
        } else {
            this.errorMessage = this.errorMessage + ", " + errorMessage;
        }
    }

    public String getSuccessMessage() {
        return successMessage;
    }

    public void setSuccessMessage(String successMessage) {
        if (StringUtils.isEmpty(this.successMessage)) {
            this.successMessage = successMessage;
        } else {
            this.successMessage = this.successMessage + ", " + successMessage;
        }
    }

    public BizStoreEntity getAddedBizStore() {
        return addedBizStore;
    }

    public void setAddedBizStore(BizStoreEntity addedBizStore) {
        this.addedBizStore = addedBizStore;
    }
}
