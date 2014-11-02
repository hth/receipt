/**
 *
 */
package com.receiptofi.repository;

import com.mongodb.DB;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.gridfs.GridFS;
import com.mongodb.gridfs.GridFSDBFile;
import com.mongodb.gridfs.GridFSInputFile;

import com.receiptofi.domain.FileSystemEntity;
import com.receiptofi.domain.shared.UploadDocumentImage;

import org.bson.types.ObjectId;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;
import java.util.List;

/**
 * @author hitender
 * @since Jan 3, 2013 3:09:08 AM
 */
public final class StorageManagerImpl implements StorageManager {
    private static final Logger LOG = LoggerFactory.getLogger(StorageManagerImpl.class);

    private final GridFS gridFs;

    public StorageManagerImpl(DB gridfsDb) {
        try {
            gridFs = new GridFS(gridfsDb);
        } catch (com.mongodb.MongoException exception) {
            LOG.error("Error in initializing MongoDB: Issue with getting the connection during server startup. " + exception.getLocalizedMessage());
            LOG.error("Receiptofi Mongo DB: " + exception.getMessage());
            throw exception;
        }
    }

    @Override
    public List<UploadDocumentImage> getAllObjects() {
        DBCursor dbCursor = gridFs.getFileList();
        while (dbCursor.hasNext()) {
            DBObject dbObject = dbCursor.next();
            gridFs.find(dbObject.toString());
        }
        throw new UnsupportedOperationException("Method not implemented");
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

    @Override
    public UploadDocumentImage findOne(String id) {
        throw new UnsupportedOperationException("Method not implemented");
    }

    private void deleteSoft(String id) {
        GridFSDBFile receiptBlob = get(id);
        receiptBlob.put("D", true);
        receiptBlob.save();
    }

    @Override
    public void deleteSoft(Collection<FileSystemEntity> fileSystemEntities) {
        for (FileSystemEntity fileSystemEntity : fileSystemEntities) {
            deleteSoft(fileSystemEntity.getBlobId());
        }
    }

    @Override
    public void deleteHard(UploadDocumentImage object) {
        throw new UnsupportedOperationException("Method not implemented");
    }

    @Override
    public void deleteHard(String id) {
        LOG.debug("deleted GridFs object - " + id);
        gridFs.remove(new ObjectId(id));
    }

    @Override
    public void deleteHard(Collection<FileSystemEntity> fileSystemEntities) {
        for (FileSystemEntity fileSystemEntity : fileSystemEntities) {
            LOG.debug("deleted GridFs object - " + fileSystemEntity.getBlobId());
            gridFs.remove(new ObjectId(fileSystemEntity.getBlobId()));
        }
    }

    private String persist(UploadDocumentImage uploadDocumentImage) {
        boolean closeStreamOnPersist = true;
        GridFSInputFile receiptBlob;
        try {
            if (uploadDocumentImage.containsFile()) {
                InputStream is = new FileInputStream(uploadDocumentImage.getFile());
                receiptBlob = gridFs.createFile(is, uploadDocumentImage.getFileName(), closeStreamOnPersist);
            } else {
                receiptBlob = gridFs.createFile(
                        uploadDocumentImage.getFileData().getInputStream(),
                        uploadDocumentImage.getFileName(),
                        closeStreamOnPersist
                );
            }
        } catch (IOException ioe) {
            LOG.error("Image persist error:{}", ioe);
            throw new RuntimeException(ioe.getCause());
        }

        if (receiptBlob == null) {
            return null;
        } else {
            receiptBlob.put("D", false);
            receiptBlob.put("FILE_TYPE", uploadDocumentImage.getFileType().getName());
            receiptBlob.setContentType(uploadDocumentImage.getFileData().getContentType());
            receiptBlob.setMetaData(uploadDocumentImage.getMetaData());

            receiptBlob.save();
            return receiptBlob.getId().toString();
        }
    }

    @Override
    public GridFSDBFile get(String id) {
        try {
            return gridFs.findOne(new ObjectId(id));
        } catch (IllegalArgumentException iae) {
            LOG.error("Submitted image id " + id + ", error message - " + iae.getLocalizedMessage());
            return null;
        }
    }

    @Override
    public GridFSDBFile getByFilename(String filename) {
        return gridFs.findOne(filename);
    }

    @Override
    public long collectionSize() {
        return getSize();
    }
}
