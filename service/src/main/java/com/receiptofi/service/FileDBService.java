package com.receiptofi.service;

import com.mongodb.gridfs.GridFSDBFile;

import com.receiptofi.domain.FileSystemEntity;
import com.receiptofi.domain.shared.UploadDocumentImage;
import com.receiptofi.repository.StorageManager;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Collection;

/**
 * User: hitender
 * Date: 4/27/13
 * Time: 12:59 PM
 */
@Service
public final class FileDBService {
    private static final Logger LOG = LoggerFactory.getLogger(FileDBService.class);

    @Autowired private StorageManager storageManager;

    /**
     * Load file from database
     *
     * @param fileId
     * @return
     */
    public GridFSDBFile getFile(String fileId) {
        return storageManager.get(fileId);
    }

    public int getFSDBSize() {
        return storageManager.getSize();
    }

    public String saveFile(UploadDocumentImage uploadReceiptImage) throws IOException {
        return storageManager.saveFile(uploadReceiptImage);
    }

    public void deleteHard(String fileId) {
        storageManager.deleteHard(fileId);
    }

    public void deleteHard(Collection<FileSystemEntity> fileId) {
        storageManager.deleteHard(fileId);
    }
}