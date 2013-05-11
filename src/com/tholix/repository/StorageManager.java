/**
 *
 */
package com.tholix.repository;

import java.io.IOException;
import java.io.InputStream;

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

	/**
	 *
	 * @param inputStream
	 * @param contentType - text/html, image/bmp, image/jpeg, video/mpeg, image/png, image/pict, image/x-quicktime, text/rtf, text/richtext
	 * @param filename
	 * @return
	 */
	public String save(InputStream inputStream, String contentType, String filename);

	public GridFSDBFile get(String id);

	public GridFSDBFile getByFilename(String filename);

	public void deleteObject(String id);

	/**
	 * Gets size of the GridFs
	 * @return
	 */
	public int getSize();
}
