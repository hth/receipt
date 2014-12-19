package com.receiptofi.domain;

import com.receiptofi.utils.FileUtil;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;
import org.springframework.web.multipart.MultipartFile;

import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.validation.constraints.NotNull;

/**
 * Store metadata of the document image.
 * User: hitender
 * Date: 12/13/13 12:47 AM
 */
@SuppressWarnings ({
        "PMD.BeanMembersShouldSerialize",
        "PMD.LocalVariableCouldBeFinal",
        "PMD.MethodArgumentCouldBeFinal",
        "PMD.LongVariable"
})
@Document (collection = "FILE_SYSTEM")
public class FileSystemEntity extends BaseEntity {
    private static final Logger LOG = LoggerFactory.getLogger(FileSystemEntity.class);

    /** Means image it aligned vertically. */
    public static final int DEFAULT_ORIENTATION_ANGLE = 0;

    @NotNull
    @Field ("BID")
    private String blobId;

    @NotNull
    @Field ("H")
    private int height;

    @NotNull
    @Field ("SH")
    private int scaledHeight;

    @NotNull
    @Field ("W")
    private int width;

    @NotNull
    @Field ("SW")
    private int scaledWidth;

    @NotNull
    @Field ("ORN")
    private int imageOrientation = DEFAULT_ORIENTATION_ANGLE;

    @NotNull
    @Field ("SEQ")
    private int sequence;

    @NotNull
    @Field ("CT")
    private String contentType;

    @NotNull
    @Field ("LN")
    private long fileLength;

    @NotNull
    @Field ("SLN")
    private long scaledFileLength;

    @NotNull
    @Field ("OFN")
    private String originalFilename;

    /** To keep bean happy. */
    public FileSystemEntity() {
        super();
    }

    public FileSystemEntity(
            String blobId,
            BufferedImage bufferedImage,
            int imageOrientation,
            int sequence,
            MultipartFile multipartFile
    ) {
        super();
        this.blobId = blobId;
        this.height = bufferedImage.getHeight();
        this.width = bufferedImage.getWidth();
        this.imageOrientation = imageOrientation;
        this.sequence = sequence;
        this.contentType = multipartFile.getContentType();
        this.fileLength = multipartFile.getSize();
        this.originalFilename = multipartFile.getOriginalFilename();

        if (!contentType.startsWith("image")) {
            try {
                this.contentType = FileUtil.detectMimeType(multipartFile.getInputStream());
                LOG.info("content-type found {} and replaced with {}", multipartFile.getContentType(), contentType);
            } catch (IOException e) {
                LOG.error("failed to get content-type reason={}", e.getLocalizedMessage(), e);
            }
        }
    }

    public String getBlobId() {
        return blobId;
    }

    public void setBlobId(String blobId) {
        this.blobId = blobId;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public int getScaledHeight() {
        return scaledHeight;
    }

    public void setScaledHeight(int scaledHeight) {
        this.scaledHeight = scaledHeight;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getScaledWidth() {
        return scaledWidth;
    }

    public void setScaledWidth(int scaledWidth) {
        this.scaledWidth = scaledWidth;
    }

    public int getImageOrientation() {
        return imageOrientation;
    }

    public void setImageOrientation(int imageOrientation) {
        this.imageOrientation = imageOrientation;
    }

    public int getSequence() {
        return sequence;
    }

    public void setSequence(int sequence) {
        this.sequence = sequence;
    }

    public String getContentType() {
        return contentType;
    }

    public long getFileLength() {
        return fileLength;
    }

    public long getScaledFileLength() {
        return scaledFileLength;
    }

    public void setScaledFileLength(long scaledFileLength) {
        this.scaledFileLength = scaledFileLength;
    }

    public String getOriginalFilename() {
        return originalFilename;
    }

    @Transient
    public void switchHeightAndWidth() {
        int tempHeight = this.height;
        this.height = this.width;
        this.width = tempHeight;
    }

    /**
     * Name of the file on cloud.
     *
     * @return
     */
    @Transient
    public String getKey() {
        return blobId + FileUtil.DOT + FileUtil.getFileExtension(originalFilename);
    }
}
