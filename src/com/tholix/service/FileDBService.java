package com.tholix.service;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.mongodb.gridfs.GridFSDBFile;

import com.tholix.domain.UploadReceiptImage;
import com.tholix.repository.StorageManager;

/**
 * User: hitender
 * Date: 4/27/13
 * Time: 12:59 PM
 */
@Service
public class FileDBService {

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

    public String saveFile(UploadReceiptImage uploadReceiptImage) throws IOException {
        return storageManager.saveFile(uploadReceiptImage);
    }

    public void deleteFile(String fileId) {
        storageManager.deleteObject(fileId);
    }
}
