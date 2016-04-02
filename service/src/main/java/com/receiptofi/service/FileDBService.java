package com.receiptofi.service;

import com.mongodb.gridfs.GridFSDBFile;

import com.receiptofi.domain.FileSystemEntity;
import com.receiptofi.domain.shared.UploadDocumentImage;
import com.receiptofi.repository.StorageManager;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Collection;

/**
 * User: hitender
 * Date: 4/27/13
 * Time: 12:59 PM
 */
@SuppressWarnings ({
        "PMD.BeanMembersShouldSerialize",
        "PMD.LocalVariableCouldBeFinal",
        "PMD.MethodArgumentCouldBeFinal",
        "PMD.LongVariable"
})
@Service
public class FileDBService {
    private static final Logger LOG = LoggerFactory.getLogger(FileDBService.class);

    private StorageManager storageManager;

    @Autowired
    public FileDBService(StorageManager storageManager) {
        this.storageManager = storageManager;
    }

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

    public void deleteHard(Collection<FileSystemEntity> fileSystems) {
        storageManager.deleteHard(fileSystems);
    }
}
