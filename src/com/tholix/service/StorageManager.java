/**
 * 
 */
package com.tholix.service;

import java.io.IOException;
import java.io.InputStream;

import com.mongodb.gridfs.GridFSDBFile;
import com.tholix.domain.UploadReceiptImage;

/**
 * @author hitender 
 * @param <T>
 * @when Jan 3, 2013 3:08:12 AM
 * 
 * For GridFsTemplate. Because of the GridFsTemplate the mongo content has been moved to receipt-servlet.xml
 * @see http://www.rainydayinn.com/dev/distributed-storage-with-mongo-gridfs-with-spring-data-mongodb/
 * 
 * Stores Receipt Image in GridFs
 */
public interface StorageManager extends RepositoryManager<UploadReceiptImage> {

	public String save(UploadReceiptImage object) throws IOException;
	public String save(InputStream inputStream, String contentType, String filename);
	public GridFSDBFile get (String id);
	public GridFSDBFile getByFilename (String filename);

}
