package com.receiptofi.domain;

import javax.validation.constraints.NotNull;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.LinkedList;

import org.apache.commons.lang3.StringUtils;

import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;
import org.springframework.format.annotation.DateTimeFormat;

/**
 * User: hitender
 * Date: 12/13/13 12:38 AM
 */
@Document(collection = "MILEAGE")
public class MileageEntity extends BaseEntity {

    @NotNull
    @Field("S")
    private int start;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    @Field("SD")
    private Date startDate;

    @Field("E")
    private int end;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    @Field("ED")
    private Date endDate;

    @NotNull
    @Field("USER_PROFILE_ID")
    private String userProfileId;

    @DBRef
    @Field("FILES")
    private Collection<FileSystemEntity> fileSystemEntities;

    /** To keep bean happy */
    public MileageEntity() {}

    public MileageEntity(FileSystemEntity fileSystemEntity, String userProfileId) {
        fileSystemEntities.add(fileSystemEntity);
        this.userProfileId = userProfileId;
        this.startDate = getCreated();
    }

    /** To be used during merge operation */
    public void mergeEndMileage(MileageEntity mileageEntity) {
        if(mileageEntity.getFileSystemEntities().size() > 0) {
            this.end = mileageEntity.getStart();
            this.endDate = mileageEntity.getStartDate();
            fileSystemEntities.add(mileageEntity.getFileSystemEntities().iterator().next());
        }
    }

    //todo write this piece of code
    public void splitEndMileage(MileageEntity mileageEntity) {
        if(mileageEntity.getFileSystemEntities().size() > 0) {
            this.end = mileageEntity.getStart();
            this.endDate = mileageEntity.getStartDate();
            fileSystemEntities.add(mileageEntity.getFileSystemEntities().iterator().next());
        }
    }

    public int getStart() {
        return start;
    }

    public void setStart(int start) {
        this.start = start;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public int getEnd() {
        return end;
    }

    public void setEnd(int end) {
        this.end = end;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
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

    public void setFileSystemEntities(Collection<FileSystemEntity> fileSystemEntities) {
        this.fileSystemEntities = fileSystemEntities;
    }

    public void addFileSystemEntities(FileSystemEntity fileSystemEntities) {
        if(this.fileSystemEntities == null) {
            this.fileSystemEntities = new ArrayList<>();
        }
        this.fileSystemEntities.add(fileSystemEntities);
    }

    @Transient
    public int getTotal() {
        if(start != 0 && end != 0) {
            return end - start;
        }
        return -1;
    }
}
