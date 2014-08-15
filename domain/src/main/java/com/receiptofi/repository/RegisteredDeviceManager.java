package com.receiptofi.repository;

import java.util.List;

import com.receiptofi.domain.RegisteredDeviceEntity;
import com.receiptofi.domain.annotation.Mobile;

/**
 * User: hitender
 * Date: 8/12/14 10:11 PM
 */
public interface RegisteredDeviceManager extends RepositoryManager<RegisteredDeviceEntity> {

    /**
     * If updates are available then return device and mark the device as inactive else return null
     * @param rid
     * @param did
     * @return
     */
    @Mobile
    RegisteredDeviceEntity lastAccessed(String rid, String did);

    /**
     * Finds or register device if not found. When not found returns false and saves the new device.
     * When found returns true.
     * @param rid
     * @param did
     * @return
     */
    @SuppressWarnings("unused")
    @Mobile
    boolean findOrRegisterWhenNotFound(String rid, String did);
}
