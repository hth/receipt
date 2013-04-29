package com.tholix.web.form;

import com.tholix.domain.BizNameEntity;
import com.tholix.domain.BizStoreEntity;

/**
 * User: hitender
 * Date: 4/22/13
 * Time: 11:52 PM
 */
public class BizForm {

    private BizNameEntity bizName;
    private BizStoreEntity bizStore;
    private String bizError;
    private String bizSuccess;

    /** To make bean happy */
    private BizForm() {}

    public static BizForm newInstance() {
        return new BizForm();
    }

    public BizNameEntity getBizName() {
        return bizName;
    }

    public void setBizName(BizNameEntity bizName) {
        this.bizName = bizName;
    }

    public BizStoreEntity getBizStore() {
        return bizStore;
    }

    public void setBizStore(BizStoreEntity bizStore) {
        this.bizStore = bizStore;
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
