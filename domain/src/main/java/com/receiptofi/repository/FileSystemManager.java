package com.receiptofi.repository;

import java.util.Collection;

import com.receiptofi.domain.FileSystemEntity;

/**
 * User: hitender
 * Date: 12/23/13 9:21 PM
 */
public interface FileSystemManager extends RepositoryManager<FileSystemEntity> {

    void deleteSoft(Collection<FileSystemEntity> fileSystemEntities);

    void deleteHard(Collection<FileSystemEntity> fileSystemEntities);
}
