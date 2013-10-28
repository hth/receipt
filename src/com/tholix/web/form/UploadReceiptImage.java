/**
 *
 */
package com.tholix.web.form;

import java.io.File;

import org.apache.commons.io.FilenameUtils;

import org.springframework.web.multipart.MultipartFile;

import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;

import com.tholix.domain.types.FileTypeEnum;

/**
 * This class acts as a form and entity. Its shared across multiple layers. Used in persisting Image file.
 *
 * File condition takes precedent over MultipartFile.
 * Note: When file is populated then code should give precedent to it otherwise MultipartFile fileData is default.
 *
 * @author hitender
 * @since Jan 3, 2013 12:56:16 AM
 *
 * @see http://www.ioncannon.net/programming/975/spring-3-file-upload-example/
 *
 *      For GridFsTemplate
 * @see http://www.rainydayinn.com/dev/distributed-storage-with-mongo-gridfs-with-spring-data-mongodb/
 */
public final class UploadReceiptImage {
    public static final String UNDER_SCORE  = "_";
    public static final String SCALED       = UNDER_SCORE + "Scaled";

    //Default is MultipartFile
	private MultipartFile fileData;

    //Has precedent if not null (if populated)
    private File file;
    private String emailId;
    private String userProfileId;
    private FileTypeEnum fileType;

    private UploadReceiptImage() { }

	public static UploadReceiptImage newInstance() {
		return new UploadReceiptImage();
	}

	public MultipartFile getFileData() {
		return fileData;
	}

	public void setFileData(MultipartFile fileData) {
		this.fileData = fileData;
	}

    public File getFile() {
        return file;
    }

    /**
     * File condition takes precedent over MultipartFile.
     * Note: When file is populated then code should give precedent to it otherwise MultipartFile fileData is default.
     *
     * @boolean returns true if file object is populated
     */
    public boolean containsFile() {
        return file != null;
    }

    public void setFile(File file) {
        this.file = file;
    }

    public String getOriginalFileName() {
        if(!containsFile()) {
            return this.fileData.getOriginalFilename();
        } else {
            return new StringBuilder()
                    .append(FilenameUtils.getBaseName(this.fileData.getOriginalFilename()))
                    .append(SCALED)
                    .append(".")
                    .append(FilenameUtils.getExtension(this.fileData.getOriginalFilename()))
                    .toString();
        }
    }

	public String getFileName() {
        if(!containsFile()) {
		    return new StringBuilder()
                    .append(getUserProfileId())
                    .append(UNDER_SCORE)
                    .append(this.fileData.getOriginalFilename())
                    .toString();
        } else {
            return new StringBuilder()
                    .append(getUserProfileId())
                    .append(UNDER_SCORE)
                    .append(FilenameUtils.getBaseName(this.fileData.getOriginalFilename()))
                    .append(SCALED)
                    .append(".")
                    .append(FilenameUtils.getExtension(this.fileData.getOriginalFilename()))
                    .toString();
        }
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

    public FileTypeEnum getFileType() {
        return fileType;
    }

    public void setFileType(FileTypeEnum fileType) {
        this.fileType = fileType;
    }

    public DBObject getMetaData() {
        DBObject metaData = new BasicDBObject();

        metaData.put("ORIGINAL_FILENAME", getOriginalFileName());
        metaData.put("EMAIL", getEmailId());
        metaData.put("USER_PROFILE_ID", getUserProfileId());
        metaData.put("EMAIL_AND_FILENAME", new StringBuilder()
                .append(getEmailId())
                .append(UNDER_SCORE)
                .append(getOriginalFileName())
                .toString());

        metaData.put("USER_PROFILE_ID_AND_FILENAME", new StringBuilder()
                .append(getUserProfileId())
                .append(UNDER_SCORE)
                .append(getOriginalFileName())
                .toString());

        return metaData;
    }
}