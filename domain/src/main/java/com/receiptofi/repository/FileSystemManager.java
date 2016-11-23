package com.receiptofi.repository;

import com.receiptofi.domain.FileSystemEntity;
import com.receiptofi.domain.value.DiskUsageGrouped;

import java.util.Collection;
import java.util.List;

/**
 * User: hitender
 * Date: 12/23/13 9:21 PM
 */
public interface FileSystemManager extends RepositoryManager<FileSystemEntity> {
    FileSystemEntity getById(String id);

    void deleteSoft(Collection<FileSystemEntity> fileSystemEntities);

    void deleteHard(Collection<FileSystemEntity> fileSystemEntities);

    List<FileSystemEntity> filesPending(String rid);

    List<DiskUsageGrouped> diskUsage(String rid);

    boolean fileWithSimilarNameDoesNotExists(String rid, String originalFilename);

    /**
     * Change orientation of image.
     *
     * @param id
     * @param orientation
     * @param height
     * @param width
     */
    void changeFSImageOrientation(String id, int orientation, int height, int width);
}
