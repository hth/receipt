package com.tholix.web.form;

import com.tholix.domain.BizNameEntity;
import com.tholix.domain.BizStoreEntity;

/**
 * User: hitender
 * Date: 4/22/13
 * Time: 11:52 PM
 */
public final class BizForm {
    private String name;
    private String address;
    private String phone;

    private String nameId;
    private String addressId;

    private String bizError;
    private String bizSuccess;

    /** To make bean happy */
    private BizForm() {}

    public static BizForm newInstance() {
        return new BizForm();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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

    public String getAddressId() {
        return addressId;
    }

    public void setAddressId(String addressId) {
        this.addressId = addressId;
    }

    public void setBizName(BizNameEntity bizName) {
        this.name = bizName.getName();
        this.nameId = bizName.getId();
    }

    public void setBizStore(BizStoreEntity bizStore) {
        this.address = bizStore.getAddress();
        this.addressId = bizStore.getId();
        this.phone = bizStore.getPhoneFormatted();
    }

    /** To be used in future for sending confirmation regarding adding Biz Name and Store success or failure */
    public String getBizError() {
        return bizError;
    }

    public void setBizError(String bizError) {
        this.bizError = bizError;
    }

    public String getBizSuccess() {
        return bizSuccess;
    }

    public void setBizSuccess(String bizSuccess) {
        this.bizSuccess = bizSuccess;
    }
}
