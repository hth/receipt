/**
 *
 */
package com.receiptofi.repository;

import com.mongodb.gridfs.GridFSDBFile;

import com.receiptofi.domain.FileSystemEntity;
import com.receiptofi.domain.shared.UploadDocumentImage;

import java.io.IOException;
import java.util.Collection;

/**
 * @author hitender
 * @see http://www.rainydayinn.com/dev/distributed-storage-with-mongo-gridfs-with-spring-data-mongodb/
 * Stores Receipt Image in GridFs
 * @since Jan 3, 2013 3:08:12 AM
 * For GridFsTemplate. Because of the GridFsTemplate the mongo content has been moved to receipt-servlet.xml
 */
public interface StorageManager extends RepositoryManager<UploadDocumentImage> {

    /**
     * Saves the image and return the bolb id
     *
     * @param object - File
     * @return String - bolbId
     * @throws IOException
     */
    String saveFile(UploadDocumentImage object) throws IOException;

    GridFSDBFile get(String id);

    GridFSDBFile getByFilename(String filename);

    /**
     * Removes the file from db
     *
     * @param id
     */
    void deleteHard(String id);

    void deleteHard(Collection<FileSystemEntity> fileSystems);

    /**
     * Add a field delete and set the value to true
     *
     * @param fileSystems
     */
    void deleteSoft(Collection<FileSystemEntity> fileSystems);

    /**
     * Gets size of the GridFs
     *
     * @return
     */
    int getSize();
}
