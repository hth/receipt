package com.tholix.web.form;

/**
 * User: hitender
 * Date: 4/22/13
 * Time: 11:52 PM
 */
public final class BizForm {
    private String name;
    private String address;
    private String phone;

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
