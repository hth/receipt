/**
 *
 */
package com.tholix.repository;

import java.io.IOException;

import com.mongodb.gridfs.GridFSDBFile;

import com.tholix.domain.UploadReceiptImage;

/**
 * @author hitender
 * @since Jan 3, 2013 3:08:12 AM
 *
 * For GridFsTemplate. Because of the GridFsTemplate the mongo content has been moved to receipt-servlet.xml
 * @see http://www.rainydayinn.com/dev/distributed-storage-with-mongo-gridfs-with-spring-data-mongodb/
 *
 * Stores Receipt Image in GridFs
 */
public interface StorageManager extends RepositoryManager<UploadReceiptImage> {

	/**
	 * Saves the image and return the bolb id
	 * @param object - File
	 * @return String - bolbId
	 * @throws IOException
	 */
	public String saveFile(UploadReceiptImage object) throws IOException;

	public GridFSDBFile get(String id);

	public GridFSDBFile getByFilename(String filename);

    /**
     * Removes the file from db
     *
     * @param id
     */
	public void deleteHard(String id);

    /**
     * Add a field delete and set the value to true
     *
     * @param id
     */
    public void deleteSoft(String id);

	/**
	 * Gets size of the GridFs
	 * @return
	 */
	public int getSize();
}
