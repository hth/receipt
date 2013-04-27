package com.tholix.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.mongodb.gridfs.GridFSDBFile;

import com.tholix.repository.StorageManager;

/**
 * User: hitender
 * Date: 4/27/13
 * Time: 12:59 PM
 */
@Service
public class ReceiptImageService {

    @Autowired private StorageManager storageManager;

    public GridFSDBFile dbFile(String fileId) {
        return storageManager.get(fileId);
    }
}
