package com.receiptofi.domain;

import javax.validation.constraints.NotNull;

import java.util.Collection;
import java.util.LinkedList;

import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

/**
 * User: hitender
 * Date: 12/13/13 12:38 AM
 */
@Document(collection = "MILEAGE")
public class MileageEntity extends BaseEntity {

    @NotNull
    @Field("START")
    private String start;

    @NotNull
    @Field("END")
    private String end;

    @NotNull
    @Field("USER_PROFILE_ID")
    private String userProfileId;

    @NotNull
    @Field("FILES")
    private Collection<FileSystemEntity> fileSystemEntities = new LinkedList<>();

    /** To keep bean happy */
    public MileageEntity() {}

    public MileageEntity(FileSystemEntity fileSystemEntity, String userProfileId) {
        fileSystemEntities.add(fileSystemEntity);
        this.userProfileId = userProfileId;
    }

    public String getStart() {
        return start;
    }

    public void setStart(String start) {
        this.start = start;
    }

    public String getEnd() {
        return end;
    }

    public void setEnd(String end) {
        this.end = end;
    }

    public String getUserProfileId() {
        return userProfileId;
    }

    public void setUserProfileId(String userProfileId) {
        this.userProfileId = userProfileId;
    }

    public Collection<FileSystemEntity> getFileSystemEntities() {
        return fileSystemEntities;
    }

    public void addFileSystemEntities(FileSystemEntity fileSystemEntities) {
        this.fileSystemEntities.add(fileSystemEntities);
    }
}
