/**
 *
 */
package com.receiptofi.domain.shared;

import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;

import com.receiptofi.domain.types.FileTypeEnum;

import org.springframework.web.multipart.MultipartFile;

/**
 * This class acts as a form and entity. Its shared across multiple layers. Used in persisting Image file.
 * File condition takes precedent over MultipartFile.
 * Note: When file is populated then code should give precedent to it otherwise MultipartFile fileData is default.
 *
 * @author hitender
 * @link http://www.ioncannon.net/programming/975/spring-3-file-upload-example/
 * For GridFsTemplate
 * @link http://www.rainydayinn.com/dev/distributed-storage-with-mongo-gridfs-with-spring-data-mongodb/
 * @since Jan 3, 2013 12:56:16 AM
 */
@SuppressWarnings ({
        "PMD.BeanMembersShouldSerialize",
        "PMD.LocalVariableCouldBeFinal",
        "PMD.MethodArgumentCouldBeFinal",
        "PMD.LongVariable"
})
public class UploadDocumentImage {
    public static final String UNDER_SCORE = "_";

    //Default is MultipartFile
    private MultipartFile fileData;
    private String rid;
    private FileTypeEnum fileType;
    private String blobId;

    private UploadDocumentImage() {
    }

    public static UploadDocumentImage newInstance() {
        return new UploadDocumentImage();
    }

    public MultipartFile getFileData() {
        return fileData;
    }

    public void setFileData(MultipartFile fileData) {
        this.fileData = fileData;
    }

    public String getOriginalFileName() {
        return fileData.getOriginalFilename();
    }

    public String getFileName() {
        return getRid() + UNDER_SCORE + fileData.getOriginalFilename();
    }

    public String getRid() {
        return rid;
    }

    public void setRid(String rid) {
        this.rid = rid;
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
        metaData.put("RID", getRid());
        metaData.put("RID_AND_FILENAME", getRid() + UNDER_SCORE + getOriginalFileName());
        return metaData;
    }

    public String getBlobId() {
        return blobId;
    }

    public void setBlobId(String blobId) {
        this.blobId = blobId;
    }

    @Override
    public String toString() {
        return "rid='" + rid +
                ", fileType=" + fileType +
                ", blobId='" + blobId;
    }
}
