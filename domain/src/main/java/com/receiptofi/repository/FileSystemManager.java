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

    void deleteSoft(Collection<FileSystemEntity> fileSystemEntities);

    void deleteHard(Collection<FileSystemEntity> fileSystemEntities);

    List<FileSystemEntity> filesPending(String rid);

    List<DiskUsageGrouped> diskUsage(String rid);
}
