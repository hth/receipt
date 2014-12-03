package com.receiptofi.service;

import com.receiptofi.domain.CloudFileEntity;
import com.receiptofi.repository.CloudFileManager;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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

    public void save(CloudFileEntity cloudFile) {
        cloudFileManager.save(cloudFile);
    }

    public void deleteSoft(CloudFileEntity cloudFile) {
        cloudFileManager.deleteSoft(cloudFile);
    }
}
