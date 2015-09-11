package com.receiptofi.domain;

import com.receiptofi.domain.annotation.Mobile;
import com.receiptofi.domain.types.DeviceTypeEnum;

import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import javax.validation.constraints.NotNull;

/**
 * User: hitender
 * Date: 8/12/14 9:32 PM
 */
@SuppressWarnings ({
        "PMD.BeanMembersShouldSerialize",
        "PMD.LocalVariableCouldBeFinal",
        "PMD.MethodArgumentCouldBeFinal",
        "PMD.LongVariable"
})
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

    @NotNull
    @Field ("DT")
    private DeviceTypeEnum deviceType;

    private RegisteredDeviceEntity(String receiptUserId, String deviceId, DeviceTypeEnum deviceType) {
        super();
        this.receiptUserId = receiptUserId;
        this.deviceId = deviceId;
        this.deviceType = deviceType;
    }

    public static RegisteredDeviceEntity newInstance(String userProfileId, String deviceId, DeviceTypeEnum deviceType) {
        return new RegisteredDeviceEntity(userProfileId, deviceId, deviceType);
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

    public DeviceTypeEnum getDeviceType() {
        return deviceType;
    }

    public void setDeviceType(DeviceTypeEnum deviceType) {
        this.deviceType = deviceType;
    }
}
