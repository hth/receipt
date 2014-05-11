package com.receiptofi.repository;

import com.receiptofi.domain.BaseEntity;
import com.receiptofi.domain.FileSystemEntity;

import java.util.Collection;

import org.springframework.data.mongodb.core.mapping.Document;

/**
 * User: hitender
 * Date: 12/23/13 9:21 PM
 */
public interface FileSystemManager extends RepositoryManager<FileSystemEntity> {
    static String TABLE = BaseEntity.getClassAnnotationValue(FileSystemEntity.class, Document.class, "collection");

    void deleteSoft(Collection<FileSystemEntity> fileSystemEntities);

    void deleteHard(Collection<FileSystemEntity> fileSystemEntities);
}
