package com.receiptofi.domain;

import javax.validation.constraints.NotNull;

import com.receiptofi.domain.annotation.Mobile;

import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

/**
 * User: hitender
 * Date: 8/12/14 9:32 PM
 */
@Mobile
@Document (collection = "REGISTERED_DEVICE")
@CompoundIndexes (value = {
        @CompoundIndex (name = "registered_device_idx", def = "{'RID': -1, 'DID': -1}", unique = true)
})
public class RegisteredDeviceEntity extends BaseEntity {

    @NotNull
    @Field ("RID")
    private String receiptUserId;

    @NotNull
    @Field ("DID")
    private String deviceId;

    private RegisteredDeviceEntity(String receiptUserId, String deviceId) {
        this.receiptUserId = receiptUserId;
        this.deviceId = deviceId;
    }

    public static RegisteredDeviceEntity newInstance(String userProfileId, String deviceId) {
        return new RegisteredDeviceEntity(userProfileId, deviceId);
    }

    public String getReceiptUserId() {
        return receiptUserId;
    }

    public void setReceiptUserId(String receiptUserId) {
        this.receiptUserId = receiptUserId;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }
}
