/**
 *
 */
package com.receiptofi.repository;

import com.mongodb.DB;
import com.mongodb.gridfs.GridFS;
import com.mongodb.gridfs.GridFSDBFile;
import com.mongodb.gridfs.GridFSInputFile;

import com.receiptofi.domain.FileSystemEntity;
import com.receiptofi.domain.shared.UploadDocumentImage;

import org.bson.types.ObjectId;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.util.Assert;

import java.io.IOException;
import java.util.Collection;

/**
 * @author hitender
 * @since Jan 3, 2013 3:09:08 AM
 */
@SuppressWarnings ({
        "PMD.BeanMembersShouldSerialize",
        "PMD.LocalVariableCouldBeFinal",
        "PMD.MethodArgumentCouldBeFinal",
        "PMD.LongVariable"
})
public final class StorageManagerImpl implements StorageManager {
    private static final Logger LOG = LoggerFactory.getLogger(StorageManagerImpl.class);

    private static final boolean CLOSE_STREAM_ON_PERSIST = true;

    private final GridFS gridFs;

    public StorageManagerImpl(DB gridfsDb) {
        try {
            gridFs = new GridFS(gridfsDb);
        } catch (com.mongodb.MongoException e) {
            LOG.error("Error initializing Receiptofi MongoDB: Issue getting connection at startup, reason={}",
                    e.getLocalizedMessage(),
                    e);
            throw e;
        }
    }

    @Override
    public int getSize() {
        return gridFs.getFileList().size();
    }

    @Override
    public void save(UploadDocumentImage object) {
        persist(object);
    }

    @Override
    public String saveFile(UploadDocumentImage object) throws IOException {
        return persist(object);
    }

    private void deleteSoft(String id) {
        Assert.hasText(id, "Id is empty");
        GridFSDBFile receiptBlob = get(id);
        Assert.notNull(receiptBlob, "receiptBlob is empty");
        receiptBlob.put("D", true);
        receiptBlob.save();
    }

    @Override
    public void deleteSoft(Collection<FileSystemEntity> fileSystems) {
        for (FileSystemEntity fileSystem : fileSystems) {
            LOG.debug("Deleting GridFs object={}", fileSystem.getBlobId());
            deleteSoft(fileSystem.getBlobId());
        }
    }

    @Override
    public void deleteHard(UploadDocumentImage object) {
        throw new UnsupportedOperationException("Method not implemented");
    }

    @Override
    public void deleteHard(String id) {
        LOG.debug("deleted GridFs object={}", id);
        gridFs.remove(new ObjectId(id));
    }

    @Override
    public void deleteHard(Collection<FileSystemEntity> fileSystems) {
        for (FileSystemEntity fileSystem : fileSystems) {
            LOG.debug("Deleting GridFs object={}", fileSystem.getBlobId());
            gridFs.remove(new ObjectId(fileSystem.getBlobId()));
        }
    }

    private String persist(UploadDocumentImage uploadDocumentImage) {
        GridFSInputFile receiptBlob;
        try {
            receiptBlob = gridFs.createFile(
                    uploadDocumentImage.getFileData().getInputStream(),
                    uploadDocumentImage.getFileName(),
                    CLOSE_STREAM_ON_PERSIST);

            Assert.notNull(receiptBlob);
        } catch (IOException | IllegalArgumentException ioe) {
            LOG.error("Image persist error:{}", ioe.getLocalizedMessage(), ioe);
            throw new RuntimeException(ioe.getCause());
        }

        receiptBlob.put("D", false);
        receiptBlob.put("FILE_TYPE", uploadDocumentImage.getFileType().getName());
        receiptBlob.setContentType(uploadDocumentImage.getFileData().getContentType());
        receiptBlob.setMetaData(uploadDocumentImage.getMetaData());

        receiptBlob.save();
        return receiptBlob.getId().toString();
    }

    @Override
    public GridFSDBFile get(String id) {
        try {
            return gridFs.findOne(new ObjectId(id));
        } catch (IllegalArgumentException iae) {
            LOG.error("Submitted image id={}, reason={}", id, iae.getLocalizedMessage(), iae);
            return null;
        }
    }

    @Override
    public GridFSDBFile getByFilename(String filename) {
        return gridFs.findOne(filename);
    }
}
