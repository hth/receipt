/**
 *
 */
package com.receiptofi.domain.shared;

import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;

import com.receiptofi.domain.types.FileTypeEnum;

import org.apache.commons.io.FilenameUtils;

import org.springframework.web.multipart.MultipartFile;

import java.io.File;

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
public final class UploadDocumentImage {
    public static final String UNDER_SCORE = "_";
    public static final String SCALED = UNDER_SCORE + "Scaled";

    //Default is MultipartFile
    private MultipartFile fileData;

    //Has precedent if not null (if populated)
    private File file;
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

    public File getFile() {
        return file;
    }

    public void setFile(File file) {
        this.file = file;
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

    public String getOriginalFileName() {
        if (containsFile()) {
            return FilenameUtils.getBaseName(fileData.getOriginalFilename()) +
                    SCALED +
                    "." +
                    FilenameUtils.getExtension(fileData.getOriginalFilename());
        } else {
            return fileData.getOriginalFilename();
        }
    }

    public String getFileName() {
        if (containsFile()) {
            return getRid() +
                    UNDER_SCORE +
                    FilenameUtils.getBaseName(fileData.getOriginalFilename()) +
                    SCALED +
                    "." +
                    FilenameUtils.getExtension(fileData.getOriginalFilename());
        } else {
            return getRid() +
                    UNDER_SCORE +
                    fileData.getOriginalFilename();
        }
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
        return "file=" + file +
                ", rid='" + rid +
                ", fileType=" + fileType +
                ", blobId='" + blobId;
    }
}
