package com.receiptofi.service;

import com.receiptofi.domain.CloudFileEntity;
import com.receiptofi.domain.FileSystemEntity;
import com.receiptofi.domain.annotation.Mobile;
import com.receiptofi.domain.types.FileTypeEnum;
import com.receiptofi.domain.value.DiskUsageGrouped;
import com.receiptofi.repository.FileSystemManager;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

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
    private static final Logger LOG = LoggerFactory.getLogger(FileSystemService.class);
    private FileSystemManager fileSystemManager;
    private CloudFileService cloudFileService;

    @Autowired
    public FileSystemService(
            FileSystemManager fileSystemManager,
            CloudFileService cloudFileService
    ) {
        this.fileSystemManager = fileSystemManager;
        this.cloudFileService = cloudFileService;
    }

    public void save(FileSystemEntity fileSystem) {
        fileSystemManager.save(fileSystem);
    }

    public FileSystemEntity getById(String id) {
        return fileSystemManager.getById(id);
    }

    /**
     * Delete all Receipt, Coupon files from S3 and skip Documents, Feedback files are they are local.
     *
     * @param fileSystems
     * @param fileType
     */
    @Mobile
    public void deleteSoft(Collection<FileSystemEntity> fileSystems, FileTypeEnum fileType) {
        Assert.notNull(fileSystems, "FileSystem collection is null");
        fileSystemManager.deleteSoft(fileSystems);

        switch (fileType) {
            case C:
            case R:
                /** Add to Cloud File for deleting from S3. */
                for (FileSystemEntity fileSystem : fileSystems) {
                    CloudFileEntity cloudFile = CloudFileEntity.newInstance(fileSystem.getKey(), fileSystem.getFileType());
                    cloudFileService.save(cloudFile);
                    LOG.info("CloudFile created key={} fileType={}", fileSystem.getKey(), fileSystem.getFileType());
                }
                break;
            case D:
            case F:
                LOG.info("Skipping cloud delete fileType={}", fileType.getDescription());
                break;
            default:
                LOG.error("Unable to delete as fileType={} undefined", fileType.getDescription());
                break;
        }
    }

    public void deleteHard(FileSystemEntity fileSystem) {
        fileSystemManager.deleteHard(fileSystem);
    }

    public void deleteHard(Collection<FileSystemEntity> fileSystems) {
        Assert.notNull(fileSystems, "FileSystem collection is null");
        fileSystemManager.deleteHard(fileSystems);
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
        List<FileSystemEntity> fileSystems = fileSystemManager.filesPending(rid);
        for (FileSystemEntity fileSystem : fileSystems) {
            pendingFileSize += fileSystem.getFileLength();
        }

        return pendingFileSize;
    }

    public boolean fileWithSimilarNameDoesNotExists(String rid, String originalFilename) {
        return fileSystemManager.fileWithSimilarNameDoesNotExists(rid, originalFilename);
    }

    void changeFSImageOrientation(String id, int orientation, int height, int width) {
        fileSystemManager.changeFSImageOrientation(id, orientation, height, width);
    }
}
