package com.receiptofi.service;

import com.receiptofi.domain.FileSystemEntity;
import com.receiptofi.repository.FileSystemManager;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collection;

/**
 * User: hitender
 * Date: 12/23/13 9:19 PM
 */
@SuppressWarnings ({
        "PMD.BeanMembersShouldSerialize",
        "PMD.LocalVariableCouldBeFinal",
        "PMD.MethodArgumentCouldBeFinal",
        "PMD.LongVariable"
})
@Service
public class FileSystemService {

    @Autowired private FileSystemManager fileSystemManager;

    public void save(FileSystemEntity fileSystemEntity) throws Exception {
        fileSystemManager.save(fileSystemEntity);
    }

    public void updateScaledFileLength(String id, long scaledFileLength) {
        fileSystemManager.updateScaledFileLength(id, scaledFileLength);
    }

    public FileSystemEntity findById(String id) {
        return fileSystemManager.findOne(id);
    }

    public void deleteSoft(Collection<FileSystemEntity> fileSystemEntities) {
        fileSystemManager.deleteSoft(fileSystemEntities);
    }

    public void deleteHard(FileSystemEntity fileSystemEntity) {
        fileSystemManager.deleteHard(fileSystemEntity);
    }

    public void deleteHard(Collection<FileSystemEntity> fileSystemEntities) {
        fileSystemManager.deleteHard(fileSystemEntities);
    }
}
