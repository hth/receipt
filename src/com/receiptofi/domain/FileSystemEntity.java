package com.receiptofi.domain;

import com.receiptofi.domain.types.DocumentOfTypeEnum;

import javax.validation.constraints.NotNull;

import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

/**
 * User: hitender
 * Date: 12/13/13 12:47 AM
 */
@Document(collection = "FILE_SYSTEM")
public class FileSystemEntity extends BaseEntity {

    @NotNull
    @Field("BLOB_ID")
    private String blobId;

    @NotNull
    @Field("ORIENTATION")
    private int imageOrientation = 0;

    @NotNull
    @Field("SEQUENCE")
    private int sequence;

    /** To keep bean happy */
    public FileSystemEntity() {}

    public FileSystemEntity(String blobId, int imageOrientation, int sequence) {
        this.blobId = blobId;
        this.imageOrientation = imageOrientation;
        this.sequence = sequence;
    }

    public String getBlobId() {
        return blobId;
    }

    public void setBlobId(String blobId) {
        this.blobId = blobId;
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
}
