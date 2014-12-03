package com.receiptofi.repository;

import com.receiptofi.domain.CloudFileEntity;

/**
 * User: hitender
 * Date: 12/2/14 6:36 PM
 */
public interface CloudFileManager extends RepositoryManager<CloudFileEntity> {

    void deleteSoft(CloudFileEntity object);
}
