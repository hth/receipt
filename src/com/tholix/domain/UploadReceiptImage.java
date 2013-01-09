/**
 * 
 */
package com.tholix.domain;

import org.springframework.web.multipart.commons.CommonsMultipartFile;

/**
 * @author hitender
 * @when Jan 3, 2013 12:56:16 AM
 * 
 * @see http://www.ioncannon.net/programming/975/spring-3-file-upload-example/
 * 
 *      For GridFsTemplate
 * @see http://www.rainydayinn.com/dev/distributed-storage-with-mongo-gridfs-with-spring-data-mongodb/
 */
public class UploadReceiptImage {

	private String description;
	private CommonsMultipartFile fileData;

	public static UploadReceiptImage newInstance() {
		return new UploadReceiptImage();
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public CommonsMultipartFile getFileData() {
		return fileData;
	}

	public void setFileData(CommonsMultipartFile fileData) {
		this.fileData = fileData;
	}

	public String getFileName() {
		return this.fileData.getOriginalFilename();
	}
}
