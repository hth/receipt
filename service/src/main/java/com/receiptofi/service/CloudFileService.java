package com.receiptofi.service;

import com.receiptofi.domain.CloudFileEntity;
import com.receiptofi.repository.CloudFileManager;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * User: hitender
 * Date: 12/3/14 3:56 AM
 */
@SuppressWarnings ({
        "PMD.BeanMembersShouldSerialize",
        "PMD.LocalVariableCouldBeFinal",
        "PMD.MethodArgumentCouldBeFinal",
        "PMD.LongVariable"
})
@Service
public class CloudFileService {

    private CloudFileManager cloudFileManager;

    @Autowired
    public CloudFileService(CloudFileManager cloudFileManager) {
        this.cloudFileManager = cloudFileManager;
    }

    /**
     * CloudFileEntity is marked as deleted.
     * @param cloudFile
     */
    public void save(CloudFileEntity cloudFile) {
        cloudFileManager.save(cloudFile);
    }

    public List<CloudFileEntity> getAllMarkedAsDeleted() {
        return cloudFileManager.getAllMarkedAsDeleted();
    }

    public void deleteHard(CloudFileEntity cloudFileEntity) {
        cloudFileManager.deleteHard(cloudFileEntity);
    }
}
