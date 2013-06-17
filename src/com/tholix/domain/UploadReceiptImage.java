/**
 *
 */
package com.tholix.domain;

import org.springframework.web.multipart.commons.CommonsMultipartFile;

import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;

/**
 * @author hitender
 * @since Jan 3, 2013 12:56:16 AM
 *
 * @see http://www.ioncannon.net/programming/975/spring-3-file-upload-example/
 *
 *      For GridFsTemplate
 * @see http://www.rainydayinn.com/dev/distributed-storage-with-mongo-gridfs-with-spring-data-mongodb/
 */
public class UploadReceiptImage {
	private CommonsMultipartFile fileData;
    private String emailId;
    private String userProfileId;

    private UploadReceiptImage() { }

	public static UploadReceiptImage newInstance() {
		return new UploadReceiptImage();
	}

	public CommonsMultipartFile getFileData() {
		return fileData;
	}

	public void setFileData(CommonsMultipartFile fileData) {
		this.fileData = fileData;
	}

    public String getOriginalFileName() {
        return this.fileData.getOriginalFilename();
    }

	public String getFileName() {
		return getUserProfileId() + "_" + this.fileData.getOriginalFilename();
	}

    public String getEmailId() {
        return emailId;
    }

    public void setEmailId(String emailId) {
        this.emailId = emailId;
    }

    public String getUserProfileId() {
        return userProfileId;
    }

    public void setUserProfileId(String userProfileId) {
        this.userProfileId = userProfileId;
    }

    public DBObject getMetaData() {
        DBObject metaData = new BasicDBObject();

        metaData.put("original_fileName", getOriginalFileName());
        metaData.put("emailId", getEmailId());
        metaData.put("userProfileId", getUserProfileId());
        metaData.put("emailId_and_fileName", getEmailId() + "_" + getOriginalFileName());
        metaData.put("userProfileId_and_fileName", getUserProfileId() + "_" + getOriginalFileName());

        return metaData;
    }
}