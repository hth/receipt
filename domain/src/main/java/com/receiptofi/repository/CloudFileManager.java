package com.receiptofi.repository;

import com.receiptofi.domain.CloudFileEntity;

import java.util.List;

/**
 * User: hitender
 * Date: 12/2/14 6:36 PM
 */
public interface CloudFileManager extends RepositoryManager<CloudFileEntity> {

    CloudFileEntity getById(String id);
    List<CloudFileEntity> getAllMarkedAsDeleted();
}
