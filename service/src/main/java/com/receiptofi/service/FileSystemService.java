package com.receiptofi.service;

import com.receiptofi.domain.FileSystemEntity;
import com.receiptofi.domain.value.DiskUsageGrouped;
import com.receiptofi.repository.FileSystemManager;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;

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

    public FileSystemEntity getById(String id) {
        return fileSystemManager.getById(id);
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

    public DiskUsageGrouped diskUsage(String rid) {
        DiskUsageGrouped diskUsageGrouped;
        List<DiskUsageGrouped> diskUsages = fileSystemManager.diskUsage(rid);
        if (diskUsages.isEmpty()) {
            diskUsageGrouped = new DiskUsageGrouped();
        } else {
            diskUsageGrouped = diskUsages.get(0);
        }
        return diskUsageGrouped;
    }

    public long filesPendingDiskUsage(String rid) {
        long pendingFileSize = 0;
        List<FileSystemEntity> fileSystemEntities = fileSystemManager.filesPending(rid);
        for (FileSystemEntity fileSystem : fileSystemEntities) {
            pendingFileSize += fileSystem.getFileLength();
        }

        return pendingFileSize;
    }

    public boolean fileWithSimilarNameDoesNotExists(String rid, String originalFilename) {
        return fileSystemManager.fileWithSimilarNameDoesNotExists(rid, originalFilename);
    }
}
