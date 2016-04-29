package com.receiptofi.repository;

import com.receiptofi.domain.RegisteredDeviceEntity;
import com.receiptofi.domain.annotation.Mobile;
import com.receiptofi.domain.types.DeviceTypeEnum;

import java.util.List;

/**
 * User: hitender
 * Date: 8/12/14 10:11 PM
 */
public interface RegisteredDeviceManager extends RepositoryManager<RegisteredDeviceEntity> {

    /**
     * Find if device is registered with receipt user.
     *
     * @param rid
     * @param did
     * @return
     */
    @Mobile
    RegisteredDeviceEntity find(String rid, String did);

    /**
     * If updates are available then return device and mark the device as inactive else return null
     *
     * @param rid
     * @param did
     * @return
     */
    @SuppressWarnings ("unused")
    @Mobile
    RegisteredDeviceEntity lastAccessed(String rid, String did);

    /**
     * Finds or register device if not found. When not found returns false and saves the new device.
     * When found returns true.
     *
     * @param rid
     * @param did
     * @return
     */
    @SuppressWarnings ("unused")
    @Mobile
    RegisteredDeviceEntity registerDevice(String rid, String did, DeviceTypeEnum deviceType, String token);

    /**
     * Get registered devices for RID.
     *
     * @param rid
     * @return
     */
    List<RegisteredDeviceEntity> getDevicesForRid(String rid);

    /**
     * Delete device info.
     *
     * @param rid
     * @param token
     */
    void deleteHard(String rid, String token);
}
